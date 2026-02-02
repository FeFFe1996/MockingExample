package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class BookingSystemTest{
    private List<Room> roomList = new ArrayList<>();
    @Mock
    private TimeProvider timeProvider;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    BookingSystem bookingSystem;

    @Test
    void bookRoomShouldReturnErrorWhenNull(){
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom(null, null, timeProvider.getCurrentTime().plusDays(4));
        });

        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", e.getMessage());
    }

    @Test
    void bookRoomDateIsBeforeToday() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("1503", timeProvider.getCurrentTime().minusDays(2), timeProvider.getCurrentTime().plusDays(2));
        });
        assertThat(e.getMessage()).isEqualTo("Kan inte boka tid i dåtid");
    }

    @Test
    void bookRoomShouldReturnErrorWhenEndDateIsBeforeStartDate() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("1503", timeProvider.getCurrentTime().plusDays(2), timeProvider.getCurrentTime());
        });
        assertThat(e.getMessage()).isEqualTo("Sluttid måste vara efter starttid");
    }

    @Test
    void bookRoomRoomNotExisting(){
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Room room = new Room("1501", "Suite");
        Mockito.doAnswer(invocation -> {
            roomList.add(room);
            return null;
        }).when(roomRepository).save(room);
        roomRepository.save(room);

        Mockito.doAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            return (Optional) roomList.stream().filter(p -> p.getId().equals(arg)).findAny();
        }).when(roomRepository).findById(anyString());

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("1502", timeProvider.getCurrentTime().plusDays(1), timeProvider.getCurrentTime().plusDays(2));
        });

        assertThat(e.getMessage()).isEqualTo("Rummet existerar inte");
    }

    @Test
    void bookRoomIsUnavailable() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Room room = new Room("1501", "2");
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Mockito.doAnswer(invocation -> {
            roomList.add(room);
            return null;
        }).when(roomRepository).save(room);

        Mockito.doAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            return (Optional) roomList.stream().filter(p -> p.getId().equals(arg)).findFirst();
        }).when(roomRepository).findById(anyString());

        roomRepository.save(room);

        bookingSystem.bookRoom("1501", timeProvider.getCurrentTime().plusDays(1), timeProvider.getCurrentTime().plusDays(3));

        boolean checkIfAvailable = bookingSystem.bookRoom("1501", timeProvider.getCurrentTime().plusDays(2), timeProvider.getCurrentTime().plusDays(4));

        assertThat(checkIfAvailable).isFalse();
    }

    @Test
    void bookRoomIsAvailable() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Room room = new Room("1501", "Suite");
        Mockito.doAnswer(invocation -> {
            roomList.add(room);
            return null;
        }).when(roomRepository).save(room);
        Mockito.doAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            return (Optional) roomList.stream().filter(p -> p.getId().equals(arg)).findFirst();
        }).when(roomRepository).findById(anyString());
        roomRepository.save(room);
        bookingSystem.bookRoom("1501", timeProvider.getCurrentTime().plusDays(1), timeProvider.getCurrentTime().plusDays(2));

        boolean checkIfAvailable = bookingSystem.bookRoom("1501", timeProvider.getCurrentTime().plusDays(3), timeProvider.getCurrentTime().plusDays(4));

        assertThat(checkIfAvailable).isTrue();
    }

    @Test
    void GetAvailableThrowErrorWhenTimeNUll() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.getAvailableRooms(null, null);
        });

        assertThat(e.getMessage()).isEqualTo("Måste ange både start- och sluttid");
    }

    @Test
    void getAvailableRoomsEndtimeBeforeStarTime() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.getAvailableRooms(timeProvider.getCurrentTime().plusDays(2), timeProvider.getCurrentTime());
        });

        assertThat(e.getMessage()).isEqualTo("Sluttid måste vara efter starttid");
    }

    @Test
    void getAvailableRoomsReturnsRoomsAvailable() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Room room = new Room("1501", "Suite");
        Room room2 = new Room("1502", "Family");
        Mockito.doAnswer(invocation -> {
            Room arg = invocation.getArgument(0);
            roomList.add(arg);
            return null;
        }).when(roomRepository).save(any(Room.class));
        Mockito.doAnswer(invocation -> {
            return roomList.stream().toList();
        }).when(roomRepository).findAll();
        Mockito.doAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            return (Optional) roomList.stream().filter(p -> p.getId().equals(arg)).findFirst();
        }).when(roomRepository).findById(anyString());

        roomRepository.save(room);
        roomRepository.save(room2);

        bookingSystem.bookRoom("1501", timeProvider.getCurrentTime().plusDays(1), timeProvider.getCurrentTime().plusDays(2));

        List<Room> availableRooms = bookingSystem.getAvailableRooms(timeProvider.getCurrentTime(), timeProvider.getCurrentTime().plusDays(2));

        assertThat(availableRooms.getFirst().getId()).isEqualTo(room2.getId());
    }
    @Test
    void getAvailableRoomsReturnsAllRooms() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Room room = new Room("1501", "Suite");
        Room room2 = new Room("1502", "Family");
        Mockito.doAnswer(invocation -> {
            Room arg = invocation.getArgument(0);
            roomList.add(arg);
            return null;
        }).when(roomRepository).save(any(Room.class));
        Mockito.doAnswer(invocation -> {
            return roomList.stream().toList();
        }).when(roomRepository).findAll();

        roomRepository.save(room);
        roomRepository.save(room2);

        List<Room> availableRooms = bookingSystem.getAvailableRooms(timeProvider.getCurrentTime(), timeProvider.getCurrentTime().plusDays(2));

        assertThat(availableRooms.size()).isEqualTo(2);
    }

    @Test
    void CancelBookingErrorWhenNUllId(){
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.cancelBooking(null);
        });

        assertThat(e.getMessage()).isEqualTo("Boknings-id kan inte vara null");
    }

    @Test
    void CancelBookingDoNothingIfReturnIsEmpty(){
        Mockito.doAnswer(invocation -> {
            Room arg = invocation.getArgument(0);
            roomList.add(arg);
            return null;
        }).when(roomRepository).save(any(Room.class));
        Mockito.doAnswer(invocation -> {
            return roomList.stream().toList();
        }).when(roomRepository).findAll();
        Room room = new Room("1501", "Suite");
        roomRepository.save(room);

        Booking booking = new Booking("1", "1501", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        room.addBooking(booking);

        assertThat(bookingSystem.cancelBooking("2")).isFalse();
    }

    @Test
    void CancelBookingShouldReturnErrorWhenCancelingDuringBooking() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Room room = new Room("1501", "Suite");
        Room room2 = new Room("1502", "Family");
        Mockito.doAnswer(invocation -> {
            Room arg = invocation.getArgument(0);
            roomList.add(arg);
            return null;
        }).when(roomRepository).save(any(Room.class));
        Mockito.doAnswer(invocation -> {
            return roomList.stream().toList();
        }).when(roomRepository).findAll();

        roomRepository.save(room);

        Booking booking = new Booking("1", "1501", LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(1));
        room.addBooking(booking);


        Exception e = assertThrows(IllegalStateException.class, () -> {
            bookingSystem.cancelBooking("1");
        });

        assertThat(e.getMessage()).isEqualTo("Kan inte avboka påbörjad eller avslutad bokning");
    }

    @Test
    void CancelBookingShouldReturnErrorWhenCancelingafterBooking() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        Room room = new Room("1501", "Suite");
        Room room2 = new Room("1502", "Family");
        Mockito.doAnswer(invocation -> {
            Room arg = invocation.getArgument(0);
            roomList.add(arg);
            return null;
        }).when(roomRepository).save(any(Room.class));
        Mockito.doAnswer(invocation -> {
            return roomList.stream().toList();
        }).when(roomRepository).findAll();

        roomRepository.save(room);

        Booking booking = new Booking("1", "1501", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        room.addBooking(booking);


        Exception e = assertThrows(IllegalStateException.class, () -> {
            bookingSystem.cancelBooking("1");
        });

        assertThat(e.getMessage()).isEqualTo("Kan inte avboka påbörjad eller avslutad bokning");
    }


}