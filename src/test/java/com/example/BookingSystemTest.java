package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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

    @BeforeEach
    void setup(){
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
    }
    @Test
    void bookRoomShouldReturnErrorWhenNull(){
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom(null, null, timeProvider.getCurrentTime().plusDays(4));
        });

        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", e.getMessage());
    }

    @Test
    void bookRoomDateIsBeforeToday() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("1503", timeProvider.getCurrentTime().minusDays(2), timeProvider.getCurrentTime().plusDays(2));
        });
        assertThat(e.getMessage()).isEqualTo("Kan inte boka tid i dåtid");
    }

    @Test
    void bookRoomShouldReturnErrorWhenEndDateIsBeforeStartDate() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("1503", timeProvider.getCurrentTime().plusDays(2), timeProvider.getCurrentTime());
        });
        assertThat(e.getMessage()).isEqualTo("Sluttid måste vara efter starttid");
    }

    @Test
    void bookRoomRoomNotExisting(){
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

        assertThat(checkIfAvailable).isEqualTo(false);
    }

    @Test
    void bookRoomIsAvailable() {
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

        assertTrue(checkIfAvailable);
    }
}