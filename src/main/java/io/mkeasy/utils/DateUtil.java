package io.mkeasy.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.springframework.util.ObjectUtils;

public class DateUtil {

	// javaScript의 경우 moments.js 를 사용하면 됩니다.
	public static long getDurTime(Date start, Date end, String timeUnit) {
		long ms = end.getTime() - start.getTime();
		if(timeUnit != null) {
			switch(timeUnit.toLowerCase()) {
			case "sec": return TimeUnit.MILLISECONDS.toSeconds(ms);
			case "min": return TimeUnit.MILLISECONDS.toMinutes(ms);
			case "hour":return TimeUnit.MILLISECONDS.toHours(ms);
			case "day":return TimeUnit.MILLISECONDS.toDays(ms);
			}
		}
		return ms;
	}

	// JAVA의 date(), calendar는 바로 사용하지 말 것 !!!
	public static Date getNow() {
		LocalDateTime datetime = LocalDateTime.now();
		return datetime.toDate();
	}

	public static Date getYesterday() {
		LocalDateTime datetime = LocalDateTime.now();
		return datetime.minusDays(1).toDate();
	}

	public static LocalDateTime getNowLocalDateTime() {
		return LocalDateTime.now();
	}

	private static String MIN_DATE = "0001-01-01";

	public static Date getDate(Date date) {
		if (date == null) {
			LocalDateTime datetime = LocalDateTime.parse(MIN_DATE); // MSSQL mininum, not allow null
			return datetime.toDate();
		}
		LocalDateTime datetime = LocalDateTime.fromDateFields(date);
		return datetime.toDate();
	}

	public static Date getDate(String date) {
		if (StringUtils.isEmpty(date)) {
			LocalDateTime datetime = LocalDateTime.parse(MIN_DATE); // MSSQL mininum, not allow null
			return datetime.toDate();
		}
		LocalDateTime datetime = LocalDateTime.fromDateFields(new Date(date));
		return datetime.toDate();
	}

	// SimpleDateFormat 형식의 date String 을 반환
	final static String DATE_FORMAT = "MM/dd/yyyy";
	public static String getFormatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String formated_date = sdf.format(date);
		return formated_date;
	}
	public static String getFormatDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String formated_date = sdf.format(getDate(date));
		return formated_date;
	}
	public static String getFormatDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String formated_date = sdf.format(date);
		return formated_date;
	}

	public static boolean isDateValid(String date) {
		try {
			DateFormat df = new SimpleDateFormat(DATE_FORMAT);
			df.setLenient(false);
			df.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public static boolean isEmpty(String date) {
		if(StringUtils.isEmpty(date))
			return true;
		if(StringUtils.equals(date, MIN_DATE))
			return true;
		return false;
	}

	public static boolean isEmpty(Date date) {
		if(ObjectUtils.isEmpty(date))
			return true;

		LocalDateTime minDate = LocalDateTime.parse(MIN_DATE); // MSSQL mininum, not allow null
		Date nowDate = getDate(date);
		if(DateUtils.isSameDay(minDate.toDate(), nowDate))
			return true;

		return false;
	}

	// 최소 일자일 경우 공백(null)으로 출력
	public static Date getValidDate(Date date) {
		if(isEmpty(date)) return null;
		return getDate(date);
	}

	public static String getDateString(Map<String, Object> item, String key_dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date _dt = getValidDate((Date) item.get(key_dt));
		String dt = "";
		if(_dt != null )
			dt = StringUtil.trimToEmpty(sdf.format(DateUtil.getValidDate((Date) item.get(key_dt))));
		return dt;
	}

	private final static Calendar newCalendar() {
		return Calendar.getInstance();
	}


	/**
	 * 현재 년도를 구한다
	 * @Method : nowYear()
	 * @return int
	 */
	public static int nowYear() {
		return newCalendar().get(Calendar.YEAR);
	}
	
	/**
	 * 현재 월을 구한다
	 * @Method : nowYear()
	 * @return int
	 */
	public static int nowMonth() {
		return newCalendar().get(Calendar.MONTH)+1;
	}

	/**
	 * 현재 일자를 구한다
	 * @Method : nowDay()
	 * @return int
	 */
	public static int nowDay() {
		return newCalendar().get(Calendar.DAY_OF_MONTH);
	}

	// 현재 시스템에 설정된, 타임존을 가져옵니다.
	// Ex : Asia/Seoul
	public static String getTimezone() {
		Calendar cal = Calendar.getInstance();
		return cal.getTimeZone().getID();
	}


	// UTC기준으로, 현재 시간을 가져옵니다
	public static Date getUTCNow() {
		DateTime dt = new DateTime(DateTimeZone.UTC);
		LocalDateTime ldt = dt.toLocalDateTime();
		return ldt.toDate();
	}

	// 국가는 여러개의 타임존을 가질 수 있습니다.
	// 타임존기준으로, 현재 시간을 가져옵니다.
	// tz = America/Los_Angeles
	public static Date getTZDate(Date date, String timezone) {
		DateTimeZone tz = DateTimeZone.forID(timezone);
		DateTime dt = new DateTime(date, tz);
		LocalDateTime ldt = dt.toLocalDateTime();
		return ldt.toDate();
	}

	public static Date getTZNow(String timezone) {
		return getTZDate(getNow(), getTimezone());
	}
	
	// 사용가능한 타임존 목록을 가져옵니다.
	public static List<String> getTimezones() {
		List<String> timeZones = new ArrayList<String>();
		for (String id : DateTimeZone.getAvailableIDs()) {
			String tz = DateTimeZone.forID(id).getID();
			timeZones.add(tz);
		}
		return timeZones;
	}

	// Iot 데이타중 날짜 형식에 맞지 않는 것들이 많습니다.
	public static String validateDateFormat(String dateToValdate) throws ParseException {

		if (dateToValdate == null)
			return null;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//To make strict date format validation
		formatter.setLenient(false);
		String parsedDate = null;
		try {
			formatter.parse(dateToValdate);
			parsedDate = dateToValdate;
		} catch (ParseException e) {
			// throw e;
		}

		return parsedDate;
	}
	
	// format example : 2015-04-14T11:07:36.639Z
	public static String getISODateTime() {
		return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
	}

	public static void main(String[] args) {
		getDate("");
	}

}
