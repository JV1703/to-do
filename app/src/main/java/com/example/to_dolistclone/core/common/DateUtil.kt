package com.example.to_dolistclone.core.common

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

class DateUtil {

    val zoneId: ZoneId = ZoneId.systemDefault()

    fun toLong(localDateTime: LocalDateTime): Long {
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli()
    }

    fun toLong(localDate: LocalDate): Long {
        return localDate.atStartOfDay().atZone(zoneId).toInstant().toEpochMilli()
    }

    fun toLocalDate(date: Date): LocalDate {
        return date.toInstant().atZone(zoneId).toLocalDate()
    }

    fun toLocalDate(date: LocalDateTime): LocalDate {
        return date.toLocalDate()
    }

    fun toLocalDate(date: String): LocalDate {
        return LocalDate.parse(date)
    }

    fun toLocalDate(
        date: String, pattern: String, locale: Locale = Locale.getDefault()
    ): LocalDate {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        formatter.withLocale(locale)
        return LocalDate.parse(date, formatter)
    }

    fun toLocalDate(date: Long): LocalDate {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date), zoneId).toLocalDate()
    }

    fun toLocalTime(date: Date): LocalTime {
        return date.toInstant().atZone(zoneId).toLocalTime()
    }

    fun toLocalTime(time: String): LocalTime {
        return LocalTime.parse(time)
    }

    fun toLocalTime(long: Long): LocalTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(long), zoneId).toLocalTime()
    }

    fun toLocalDateTime(date: LocalDate, time: LocalTime): LocalDateTime {
        return LocalDateTime.of(date, time)
    }

    fun toLocalDateTime(long: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(long), zoneId)
    }

    fun toLocalDateTime(localDate: LocalDate): LocalDateTime {
        return localDate.atStartOfDay()
    }

    fun toString(date: LocalDate, pattern: String, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return date.format(formatter)
    }

    fun toString(time: LocalTime, pattern: String, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return time.format(formatter)
    }

    fun toString(dateTime: LocalDateTime, pattern: String, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return dateTime.format(formatter)
    }

    fun toString(long: Long, pattern: String, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return toLocalDateTime(long).format(formatter)
    }

    fun getCurrentDateTime(): LocalDateTime {
        return LocalDateTime.now(zoneId)
    }

    fun getCurrentDateTimeLong(): Long {
        return LocalDateTime.now().atZone(zoneId).toInstant().toEpochMilli()
    }

    fun getZonedDateTime(dateTime: LocalDateTime, timeZoneId: ZoneId = zoneId): ZonedDateTime {
        return dateTime.atZone(timeZoneId)
    }

    fun getCurrentDate(): LocalDate {
        return LocalDate.now(zoneId)
    }

    fun getFirstDayOfWeek(): DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

    fun getFirstDateOfWeek(date: Long): LocalDate {
        val currentDate = toLocalDate(date)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        return currentDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    }

    fun getFirstDateOfWeek(date: LocalDate): LocalDate {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        return date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    }

    fun getLastDateOfWeek(date: Long): LocalDate {
        val currentDate = toLocalDate(date)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val lastDayOfWeek = DayOfWeek.of(((firstDayOfWeek.value + 5) % DayOfWeek.values().size) + 1)
        return currentDate.with(TemporalAdjusters.nextOrSame(lastDayOfWeek))
    }

    fun getFirstDateOfMonth(date: Long): LocalDate {
        val selectedDate = toLocalDate(date)
        return selectedDate.withDayOfMonth(1)
    }

    fun getLastDateOfMonth(date: Long): LocalDate {
        val selectedDate = getFirstDateOfMonth(date)
        val isLeapYear = selectedDate.isLeapYear
        val lengthOfMonth = selectedDate.month.length(isLeapYear)
        return selectedDate.withDayOfMonth(lengthOfMonth)
    }

    fun generateDaysInWeek(startDay: DayOfWeek): List<DayOfWeek> {
        return when (startDay) {
            DayOfWeek.SUNDAY -> {
                listOf(
                    DayOfWeek.SUNDAY,
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY
                )
            }
            DayOfWeek.MONDAY -> {
                listOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
                )
            }
            DayOfWeek.TUESDAY -> {
                listOf(
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY,
                    DayOfWeek.MONDAY
                )
            }
            DayOfWeek.WEDNESDAY -> {
                listOf(
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY,
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY
                )
            }
            DayOfWeek.THURSDAY -> {
                listOf(
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY,
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY
                )
            }
            DayOfWeek.FRIDAY -> {
                listOf(
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY,
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY
                )
            }
            DayOfWeek.SATURDAY -> {
                listOf(
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY,
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY
                )
            }
        }
    }

}