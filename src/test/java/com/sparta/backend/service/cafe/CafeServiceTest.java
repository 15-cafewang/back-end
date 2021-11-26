package com.sparta.backend.service.cafe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CafeServiceTest {

    @InjectMocks
    CafeService cafeService;

    @Nested
    @DisplayName("getTimeZone()테스트")
    class name{
        @Test
        @DisplayName("아침시간")
        void morningInput() {
            //given
            //08시
            LocalDateTime now = LocalDateTime.of(LocalDateTime.now().getYear(),
                    LocalDateTime.now().getMonth(),
                    LocalDateTime.now().getDayOfMonth(),
                    8, 0, 0, 0);
            //when
            List<LocalDateTime> resultTime = cafeService.getTimeZone(now);

            //then
            assertEquals(5, resultTime.get(0).getHour());
            assertEquals(11, resultTime.get(1).getHour());
        }

        @Test
        @DisplayName("점심시간")
        void LunchInput() {
            //given
            //08시
            LocalDateTime now = LocalDateTime.of(LocalDateTime.now().getYear(),
                    LocalDateTime.now().getMonth(),
                    LocalDateTime.now().getDayOfMonth(),
                    13, 0, 0, 0);
            //when
            List<LocalDateTime> resultTime = cafeService.getTimeZone(now);

            //then
            assertEquals(11, resultTime.get(0).getHour());
            assertEquals(16, resultTime.get(1).getHour());
        }

        @Test
        @DisplayName("저녁시간")
        void dinnerInput() {
            //given
            //08시
            LocalDateTime now = LocalDateTime.of(LocalDateTime.now().getYear(),
                    LocalDateTime.now().getMonth(),
                    LocalDateTime.now().getDayOfMonth(),
                    18, 0, 0, 0);
            //when
            List<LocalDateTime> resultTime = cafeService.getTimeZone(now);

            //then
            assertEquals(16, resultTime.get(0).getHour());
            assertEquals(20, resultTime.get(1).getHour());
        }

        @Test
        @DisplayName("밤시간")
        void nightInput() {
            //given
            //08시
            LocalDateTime now = LocalDateTime.of(LocalDateTime.now().getYear(),
                    LocalDateTime.now().getMonth(),
                    LocalDateTime.now().getDayOfMonth(),
                    23, 0, 0, 0);
            //when
            List<LocalDateTime> resultTime = cafeService.getTimeZone(now);

            //then
            assertEquals(20, resultTime.get(0).getHour());
            assertEquals(4, resultTime.get(1).getHour());
        }

        @Test
        @DisplayName("밤시간-12시 이후")
        void nightAfter12Input() {
            //given
            //08시
            LocalDateTime now = LocalDateTime.of(LocalDateTime.now().getYear(),
                    LocalDateTime.now().getMonth(),
                    LocalDateTime.now().getDayOfMonth(),
                    03, 0, 0, 0);
            //when
            List<LocalDateTime> resultTime = cafeService.getTimeZone(now);

            //then
            assertEquals(20, resultTime.get(0).getHour());
            assertEquals(4, resultTime.get(1).getHour());
            assertEquals(LocalDateTime.now().getDayOfMonth() - 1, resultTime.get(0).getDayOfMonth());
        }
    }
}