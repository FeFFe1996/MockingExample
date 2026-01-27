package com.example;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookingSystemTest{
    private List<Room> roomList = new ArrayList<>();
    @Mock
    private TimeProvider timeProvider = new TimeProvider() {
        @Override
        public LocalDateTime getCurrentTime() {
            return LocalDateTime.now();
        }
    };
    @Mock
    private RoomRepository roomRepository = new RoomRepository() {
        @Override
        public Optional<Room> findById(String id) {
            Optional<Room> optList = roomList.stream().filter(r -> r.getId().equals(id)).findAny();
            return optList;
        }

        @Override
        public List<Room> findAll() {
            return roomList.stream().toList();
        }

        @Override
        public void save(Room room) {
            roomList.add(room);
        }
    };

    @Mock
    private NotificationService notificationService = new NotificationService() {
        @Override
        public void sendBookingConfirmation(Booking booking) throws NotificationException {

        }

        @Override
        public void sendCancellationConfirmation(Booking booking) throws NotificationException {

        }
    };


    BookingSystem bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);

    @Test
    void bookRoomShouldReturnErrorWhenNull(){
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
           bookingSystem.bookRoom(null, null, null);
        });

        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", e.getMessage());
    }

    @Test
    void bookRoomShouldReturnErrorWhenStartDateIsBeforeToday() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
           bookingSystem.bookRoom("1503", timeProvider.getCurrentTime().minusDays(2), timeProvider.getCurrentTime().plusDays(2));
        });
        assertEquals("Kan inte boka tid i dåtid", e.getMessage());
    }

    @Test
    void bookRoomShouldReturnErrorWhenEndDateIsBeforeStartDate() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("1503", timeProvider.getCurrentTime().plusDays(2), timeProvider.getCurrentTime());
        });
        assertEquals("Sluttid måste vara efter starttid", e.getMessage());
    }

    @Test
    void bookRoomNonExistentRoomShouldReturnError(){
        Room room = new Room("1501", "Suite");
        roomRepository.save(room);
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("1502", timeProvider.getCurrentTime().plusDays(1), timeProvider.getCurrentTime().plusDays(2));
        });

        assertEquals("Rummet existerar inte", e.getMessage());
    }

}