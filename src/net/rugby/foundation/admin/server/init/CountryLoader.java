package net.rugby.foundation.admin.server.init;

import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.model.shared.Country;

public class CountryLoader {
	

	public Boolean Run(ICountryFactory cf) {
		Country c = new Country(5001L, "New Zealand", "NEW ZEALAND", "NZL", "All Blacks");
		cf.put(c);
		
		c =  new Country(5002L, "South Africa", "SOUTH AFRICA", "RSA", "Springboks");
		cf.put(c);

		c =  new Country(5003L, "Australia", "AUSTRALIA", "AUS", "Wallabies");
		cf.put(c);

		c =  new Country(5004L, "France", "FRANCE", "FRA", "Les Bleus");
		cf.put(c);

		c =  new Country(5005L, "England", "ENGLAND", "ENG", "Roses");
		cf.put(c);
		
		return true;

//		SOUTHAFRICA ("South Africa", "RSA"),
//		AUSTRALIA ("Australia", "AUS"),
//		FRANCE ("France", "FRA"),
//		ENGLAND	("England", "ENG");
	}



	private String longName;
	private String abbr;



//public static country getScrumCountry(String str) {
//	foreach (country c :  )
//	return null;
//	
//}
//IRELAND	80.22
//WALES	78.95
//SAMOA	78.71
//ARGENTINA	78.71
//ITALY	76.24
//TONGA	76.10
//	SCOTLAND	75.83
//FIJI	71.52
//	CANADA	71.41
//JAPAN	70.09
//USA	68.32
//GEORGIA	65.83
//SPAIN	63.09
//	ROMANIA	62.12
//RUSSIA	61.49
//PORTUGAL	59.63
//URUGUAY	59.37
//BELGIUM	59.17
//NAMIBIA	58.45
//CHL	57.02
//POLAND	56.91
//KOREA	56.72
//HONG KONG	55.49
//GERMANY	54.52
//ZIMBABWE	54.03
//UKRAINE	53.80
//MOROCCO	52.35
//BRAZIL	52.18
//SWEDEN	51.91
//KAZAKHSTAN	51.28
//KENYA	50.28
//PARAGUAY	50.06
//NETHERLANDS	49.83
//MOLDOVA	49.53
//MALTA	48.87
//UGANDA	48.76
//MADAGASCAR	48.19
//TUNISIA	48.18
//LITHUANIA	47.84
//CROATIA	47.76
//IVORY COAST	47.67
//CZECH REPUBLIC	47.66
//SRI LANKA	47.60
//SENEGAL	47.14
//BERMUDA	46.78
//PAPUA NEW GUINEA	46.55
//SWITZERLAND	46.16
//ISRAEL	46.06
//TRINIDAD & TOBAGO	44.68
//COOK ISLANDS	44.61
//PHILIPPINES	44.34
//COLOMBIA	43.65
//GUYANA	43.56
//THAILAND	43.44
//CHINESE TAIPEI	43.19
//ANDORRA	42.15
//VENEZUELA	41.83
//MALAYSIA	41.37
//CAYMAN	41.27
//SINGAPORE	41.19
//DENMARK	41.16
//BARBADOS	41.01
//INDIA	40.81
//CHINA	40.73
//SOLOMON ISLANDS	40.70
//NIUE ISLANDS	40.45
//LATVIA	40.05
//PERU	39.67
//SERBIA	39.49
//MEXICO	39.38
//ZAMBIA	38.87
//BOTSWANA	38.65
//SLOVENIA	38.45
//PAKISTAN	38.38
//CAMEROON	38.33
//BULGARIA	38.12
//JAMAICA	37.08
//HUNGARY	36.97
//ST. VINCENT & THE GRENADINES	36.84
//TAHITI	36.25
//AUSTRIA	36.20
//GUAM	35.70
//BAHAMAS	35.68
//SWAZILAND	35.63
//BOSNIA & HERZEGOVINA	35.29
//NIGERIA	35.29
//MONACO	35.17
//NORWAY	34.33
//LUXEMBOURG	34.25
//VANUATU	33.45
//FINLAND	26.29
}
