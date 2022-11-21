package com.example.to_dolistclone.core.common

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class DateUtil {

    val zoneId: ZoneId = ZoneId.systemDefault()

    fun toLong(localDateTime: LocalDateTime): Long {
        return localDateTime.atZone(zoneId).toEpochSecond()
    }

    fun toLong(localDate: LocalDate): Long {
        return localDate.atStartOfDay().atZone(zoneId).toEpochSecond()
    }

    fun toLocalDate(date: Date): LocalDate {
        return date.toInstant().atZone(zoneId).toLocalDate()
    }

    fun toLocalDate(date: String): LocalDate {
        return LocalDate.parse(date)
    }

    fun toLocalDate(date: Long): LocalDate {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(date), zoneId).toLocalDate()
    }

    fun toLocalTime(date: Date): LocalTime {
        return date.toInstant().atZone(zoneId).toLocalTime()
    }

    fun toLocalTime(time: String): LocalTime {
        return LocalTime.parse(time)
    }

    fun toLocalTime(long: Long): LocalTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(long), zoneId).toLocalTime()
    }

    fun toLocalDateTime(date: LocalDate, time: LocalTime): LocalDateTime {
        return LocalDateTime.of(date, time)
    }

    fun toLocalDateTime(long: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(long), zoneId)
    }

    fun toLocalDateTime(localDate: LocalDate): LocalDateTime{
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
        return LocalDateTime.now().atZone(zoneId).toLocalDateTime()
    }

    fun getCurrentDateTimeLong(): Long {
        return LocalDateTime.now().atZone(zoneId).toEpochSecond()
    }

    fun getZonedDateTime(dateTime: LocalDateTime, timeZoneId: ZoneId = zoneId): ZonedDateTime {
        return dateTime.atZone(timeZoneId)
    }

    fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }

}