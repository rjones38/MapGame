import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.fhpotsdam.unfolding.utils.*;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.*;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Engine extends PApplet {
	UnfoldingMap terrMap;
	UnfoldingMap hdiMap;
	UnfoldingMap currentMap;
	UnfoldingMap popMap;
	UnfoldingMap polMap;
	HashMap<String, DataEntry> bdr;
	HashMap<String, DataEntry> pop;
	HashMap<String, DataEntry> dataEntriesMap;
	HashMap<String, String> countryMap;
	List<Marker> countryMarkers;
	Marker selectedCountry;
	PImage frame;
	PImage key_S;
	PImage missing;
	PImage hdiNS;
	PImage key;
	PImage hdiBS;
	PImage polBS;
	PImage polS;
	PImage polNS;
	PImage terrBS;
	PImage terrS;
	PImage terrNS;
	PImage hdiS;
	PImage popBS;
	PImage popS;
	PImage popNS;
	boolean countrySelected;
	boolean keyUp = false;
	boolean drawKey = false;
	PFont f;
	int globalLife;
	String currentCountryName;
	PFont keyFont;
	Random random;
	

	 public void setup() {
		 pop = loadPopulationFromCSV(this.getClass().getResource("/resources/data/csv/population.csv").toString());
		 countryMap = new HashMap<String, String>();
		 invertMap();
		 countrySelected = false;
		 size(1200, 1000, P2D);
		 keyFont = createFont("Arial",8);
		 f = createFont("Arial",16,true);
		 textFont(f,36);
		 bdr = loadLifeFromCSV(this.getClass().getResource("/resources/data/csv/lifeexpectancy.csv").toString());
		 terrMap = new UnfoldingMap(this, new Microsoft.AerialProvider());
		 hdiMap = new UnfoldingMap(this, new Microsoft.RoadProvider());
		 currentMap = new UnfoldingMap(this, new Microsoft.AerialProvider());
		 polMap = new UnfoldingMap(this, new Microsoft.RoadProvider());
		 popMap = new UnfoldingMap(this, new Microsoft.RoadProvider());
		 MapUtils.createDefaultEventDispatcher(this, currentMap);
		 MapUtils.createDefaultEventDispatcher(this, hdiMap);
		 MapUtils.createDefaultEventDispatcher(this, terrMap);
		 MapUtils.createDefaultEventDispatcher(this, polMap);
		 MapUtils.createDefaultEventDispatcher(this, popMap);
	  
		 try {
			 loadImages();
		 } catch (MalformedURLException e) {
		// TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	  
	 
		 hdiBS = hdiNS;
		 terrBS = terrS;
		 polBS = polNS;
		 popBS = popNS;
		 List<Feature> countries = GeoJSONReader.loadData(this, this.getClass().getResource("/resources/data/json/countries.geo.json").toString());
		 random = new Random();
		 countryMarkers = MapUtils.createSimpleMarkers(countries);
		 hdiMap.addMarkers(countryMarkers);
		 polMap.addMarkers(countryMarkers);
		 popMap.addMarkers(countryMarkers);
	 
	  
	}
	 
	public void draw() {
		
	  currentMap.draw();
	  drawButtons();
	  update();
	}
	public void keyPressed() {
	}
	public void update(){
		if(terrBS == terrS){
			if(currentMap != terrMap){
				currentMap = terrMap;
			}
			
		}
		if(hdiBS == hdiS){
			if(currentMap != hdiMap){
				currentMap = hdiMap;
			}
			
			
		}
		if(polBS == polS){
			if(currentMap != polMap){
				currentMap = polMap;
			}
		}
		if(popBS == popS){
			if(currentMap != popMap){
				currentMap = popMap;
			}
		}
	}
	public PImage getFlag(Marker m){
		try{
			
			PImage i = loadImage(this.getClass().getResource("/resources/data/flags/") + m.getId().toLowerCase() + ".png");
			i.resize(300, 100);
			return i;
		} catch(Exception e){
			return missing;
		}
		
	}
	public void drawButtons(){
		image(terrBS,0, 900);
		image(hdiBS,300, 900);
		image(popBS, 600, 900);
		image(polBS, 900, 900);
		for(Marker m : currentMap.getMarkers()){
			if(m.isSelected()){
				image(getFlag(m), 450, 800);
				fill(0);
				try{
					
					image(frame,450,800);
					textFont(f,36);
					fill(0,0,0);
					if(polBS == polS){
						text(countryMap.get(m.getId()),540,800);
					}else if(popBS == popS){
						String value = Float.toString(pop.get(m.getId()).value);
						int val = new BigDecimal(value).intValue();
						text(countryMap.get(m.getId()) + "'s Population: " + Integer.toString(val),400,790);
					}else if(hdiBS == hdiS){
						text("Life Expectancy of " + countryMap.get(m.getId())+ " :" + Integer.toString(globalLife) + " years.", 300, 790);
					}
					
				}catch(Exception e){
					text("Missing Data", 500, 770);
				}
				
			}
		}
		if(drawKey){
			if(keyUp){
			
				image(key_S, 0,0 );
				textFont(f,36);
				fill(0,0,0);
				text("KEY: ", 100, 600);
				if(popBS == popS){
					textFont(f,20);
					fill(color(0,0,255));
					rect(125,610,25,25);
					fill(0);
					text("High Population", 170,630);
					fill(color(255,255,0));
					rect(125,645,25,25);
					fill(0);
					text("Low Population",170,665);
					fill(0,0,0);
					rect(125,680,25,25);
					text("No Data/Missing Data", 170, 700);
					
				}else{
					fill(0,255,0);
					rect(125,610,25,25);
					textFont(f,20);
					fill(0);
					text("High Ranking on HDI Index", 170, 630);
					fill(255,0,0);
					rect(125,645,25,25);
					fill(0);
					text("Low Ranking on HDI Index.",170,665);
					rect(125,680,25,25);
					text("No Data/Missing Data", 170,700);
					
				}
			}else{
				image(key,0,0);
				
			}
			
		}
	}
	public void mousePressed(){
		
	}
	public void mouseReleased(){
		
	}
	public void mouseMoved(){
		for (Marker marker : currentMap.getMarkers()) {
			marker.setSelected(false);
			countrySelected = false;
		}
		Marker marker = currentMap.getFirstHitMarker(mouseX, mouseY);
		if (marker != null){
			String country = marker.getId();
			
			countrySelected = true;
			//System.err.println(marker.getId());
			DataEntry d = bdr.get(country);
			try{
				globalLife = d.life;
			}catch(Exception e){
			}
			
			marker.setSelected(true);
		}
	}
	public void mouseClicked(){
		if(mouseX < 100 && mouseX > 0 && mouseY >0 && mouseY < 50){
			if(keyUp){
				keyUp = false;
			}else{
				keyUp = true;
			}
		}
	
		if((mouseX < 600 && mouseX > 300 && mouseY > 900 && mouseY < 1000)){
			if(hdiBS == hdiS){
				hdiBS = hdiNS;
				terrBS = terrS;
				drawKey = false;
			}else{
				terrBS = terrNS;
				hdiBS = hdiS;
				polBS = polNS;
				popBS = popNS;
				HashMap<String, DataEntry> hdi = loadHDIfromCSV(this.getClass().getResource("/resources/data/csv/HDI-Rank.csv").toString());
				double max = 187;
				double ratio = 255.0 / max;
				for(Marker m : hdiMap.getMarkers()){
					String country = m.getId();
					DataEntry d = hdi.get(country);
					if(d == null){
						m.setColor(color(0,0,0,0));
					}else{
						Float value = (d.value);
						double normalized = value*ratio;
						float alpha = map((int)normalized,0,255,0,255);
						m.setColor(color(alpha,255-alpha,0,200));
					}
				}
			}
		}
		if((mouseX < 300 && mouseX > 0 && mouseY > 900 && mouseY < 1000)){
				terrBS = terrS;
				hdiBS = hdiNS;
				polBS = polNS;
				popBS = popNS;
				drawKey = false;
		}
		if((mouseX < 900 && mouseX > 600 && mouseY > 900 && mouseY < 1000)){
			
			if(popBS == popS){
				popBS = popNS;
				terrBS = terrS;
				drawKey = false;
			}else{
				terrBS = terrNS;
				hdiBS = hdiNS;
				polBS = polNS;
				popBS = popS;		//rest of this block is for calculating the shade for each country and applying it
				pop = loadPopulationFromCSV(this.getClass().getResource("/resources/data/csv/population.csv").toString());
				double max = 200000000;
				double ratio = 255.0 / max;
				for(Marker m : popMap.getMarkers()){
					String country = m.getId();
				    
					DataEntry d = pop.get(country);
					if(d==null){
						m.setColor(color(0,0,0,0));
					}else{
						String value = Float.toString(d.value);
						int val = new BigDecimal(value).intValue();
						double normalized = val * ratio;
						float alpha = map((int)normalized,0,255,0,255); //alpha technique comes from their tutorial on chloropleth
						m.setColor(color(255-(int)alpha,255-(int)alpha,(int)alpha,200));
					}
					
				}
			}
				
		}
		if((mouseX < 1200 && mouseX > 900 && mouseY > 900 && mouseY < 1000)){
			if(polBS == polS){
				polBS = polNS;
				terrBS = terrS;
				drawKey = false;
			}else{
				drawKey = false;
				terrBS = terrNS;
				hdiBS = hdiNS;
				polBS = polS;
				popBS = popNS;
				for(Marker m : polMap.getMarkers()){
					m.setColor(color(random.nextInt(240), random.nextInt(240), random.nextInt(240), 200));
					m.setStrokeColor(color(255,0,0,200));
				}
				
			}
		}
		if(hdiBS == hdiS || popBS == popS){
			drawKey = true;
		}
		
	}
	public HashMap<String, DataEntry> loadPopulationFromCSV(String fileName) { //this method is a modified version of the unfoldingmaps api method which didnt work
		HashMap<String, DataEntry> dataEntriesMap = new HashMap<String, DataEntry>();

		String[] rows = loadStrings(fileName);
		int i = 0;
		for (String row : rows) {
			i++;
			if (i == 1) {
				continue;
			}
			String[] columns1 = row.split(";");
			String col = columns1[0];
			String[] columns = col.split(",");
				DataEntry dataEntry = new DataEntry();
				dataEntry.countryName = columns[0];
				dataEntry.id = columns[1].trim();
				dataEntry.value = Float.parseFloat(columns[3]);
				//System.out.println(dataEntry.value);
				dataEntriesMap.put(dataEntry.id, dataEntry);
		}

		return dataEntriesMap;
	}
	public int findMax(HashMap<String, DataEntry> x){
		int max = 0;
		for(Marker m : currentMap.getMarkers()){
			int val = new BigDecimal(x.get(m.getId()).value.intValue()).intValue();
			if(val > max){
				max = val;
			}
		}
		
		return max;
	}
	public HashMap<String, DataEntry> loadHDIfromCSV(String filename){
		HashMap<String, DataEntry> map = new HashMap<String, DataEntry>();
		String[] rows = loadStrings(filename);
		int i = 0;
		for(String row : rows){
			i++;
			if(row.equals("x")){
				continue;
			}
			if(i == 1){
				continue;
			}
			if(i >= 190){
				break;
			}
			
			String[] cols = row.split(",");
			
			//String[] columns1 = cols.split(",");
			
			DataEntry d = new DataEntry();
			
			d.countryName = cols[0].trim();
			
			d.value = (float)Integer.parseInt(cols[1].trim());
			HashMap<String, String> codes = generateCountryCode();
			d.id = codes.get(cols[0].trim());
			map.put(d.id, d);
			
		}
		return map;
		
	}
	public HashMap<String, DataEntry> loadLifeFromCSV(String filename){
		HashMap<String, DataEntry> map = new HashMap<String, DataEntry>();
		
		String[] rows = loadStrings(filename);
		int i = 0;
		for(String row : rows){
			
			i ++;
			if(i == 1){
				continue;
			}
			String[] cols = row.split(",");
			double x = Double.parseDouble(cols[2].trim());
			DataEntry d = new DataEntry();
			d.countryName = cols[0].trim();
			d.id = cols[1].trim();
			d.life = (int) x;
			map.put(d.id, d);
		}
		return map;
	}
	
	public void loadImages() throws MalformedURLException{
		  key_S = loadImage(this.getClass().getResource("/resources/data/ui/key_S.png").toString());
		  hdiNS = loadImage(this.getClass().getResource("/resources/data/ui/hdi_NS.png").toString());
		  hdiS = loadImage(this.getClass().getResource("/resources/data/ui/hdi_S.png").toString());
		  key = loadImage(this.getClass().getResource("/resources/data/ui/key.png").toString());
		  terrNS = loadImage(this.getClass().getResource("/resources/data/ui/terr_NS.png").toString());
		  terrS = loadImage(this.getClass().getResource("/resources/data/ui/terr_S.png").toString());
		  polS = loadImage(this.getClass().getResource("/resources/data/ui/pol_S.png").toString());
		  polNS = loadImage(this.getClass().getResource("/resources/data/ui/pol_NS.png").toString());
		  popS = loadImage(this.getClass().getResource("/resources/data/ui/pop_S.png").toString());
		  popNS = loadImage(this.getClass().getResource("/resources/data/ui/pop_NS.png").toString());
		  missing = loadImage(this.getClass().getResource("/resources/data/ui/missing.png").toString());
		  frame = loadImage(this.getClass().getResource("/resources/data/ui/frame.png").toString());
	 }
	public HashMap<String, String> generateCountryCode(){ //this map method comes from a tutorial online, shouldve used a bidirectional map though, this one causes problems
		 HashMap<String, String> map = new HashMap<String, String>();
		 map.put("Andorra, Principality Of", "AND");
	     map.put("United Arab Emirates", "ARE");
	     map.put("Afghanistan", "AFG");
	     map.put("Antigua And Barbuda", "ANG");
	     map.put("Anguilla", "AI");
	     map.put("Albania", "ALB");
	     map.put("Armenia", "ARM");
	     map.put("Netherlands Antilles", "ANT");
	     map.put("Angola", "AGO");
	     map.put("Antarctica", "ATA");
	     map.put("Argentina", "ARG");
	     map.put("American Samoa", "AS");
	     map.put("Austria", "AUT");
	     map.put("Australia", "AUS");
	     map.put("Aruba", "AW");
	     map.put("Azerbaijan", "AZE");
	     map.put("Bosnia", "BIH");
	     map.put("Barbados", "BBD");
	     map.put("Bangladesh", "BGD");
	     map.put("Belgium", "BEL");
	     map.put("Burkina Faso", "BFA");
	     map.put("Bulgaria", "BGR");
	     map.put("Bahrain", "BH");
	     map.put("Burundi", "BDI");
	     map.put("Benin", "BEN");
	     map.put("Bermuda", "BMA");
	     map.put("Brunei Darussalam", "BN");
	     map.put("Bolivia", "BOL");
	     map.put("Brazil", "BRA");
	     map.put("Bahamas", "BHS");
	     map.put("Bhutan", "BTN");
	     map.put("Bouvet Island", "BV");
	     map.put("Botswana", "BWA");
	     map.put("Belarus", "BLR");
	     map.put("Belize", "BZE");
	     map.put("Canada", "CAN");
	     map.put("Cocos (Keeling) Islands", "CC");
	     map.put("Central African Republic", "CAF");
	     map.put("Democratic Republic of Congo", "COD");
	     map.put("Congo", "COG");
	     map.put("Switzerland", "CH");
	     map.put("Ivory Coast", "CIV");
	     map.put("Tajikistan", "TJK");
	     map.put("Cook Islands", "CK");
	     map.put("Chile", "CHL");
	     map.put("Cameroon", "CMR");
	     map.put("China", "CHN");
	     map.put("Colombia", "COL");
	     map.put("Costa Rica", "CRI");
	     map.put("Former Czechoslovakia", "CS");
	     map.put("Cuba", "CUB");
	     map.put("Cape Verde", "CV");
	     map.put("Christmas Island", "CX");
	     map.put("Cyprus", "CYP");
	     map.put("Czech Republic", "CZE");
	     map.put("Germany", "DEU");
	     map.put("Djibouti", "DJI");
	     map.put("Denmark", "DNK");
	     map.put("Dominica", "DM");
	     map.put("Dominican Republic", "DOM");
	     map.put("Algeria", "DZA");
	     map.put("Ecuador", "ECU");
	     map.put("Estonia", "EST");
	     map.put("Egypt", "EGY");
	     map.put("Western Sahara", "EH");
	     map.put("Eritrea", "ERI");
	     map.put("Spain", "ES");
	     map.put("Ethiopia", "ETH");
	     map.put("Finland", "FIN");
	     map.put("Fiji", "FJ");
	     map.put("Falkland Islands", "FLK");
	     map.put("Micronesia", "FM");
	     map.put("Faroe Islands", "FO");
	     map.put("France", "FRA");
	     map.put("France (European Territory)", "FX");
	     map.put("Gabon", "GAB");
	     map.put("Great Britain", "GBR");
	     map.put("Grenada", "GD");
	     map.put("Georgia", "GEO");
	     map.put("French Guyana", "GUF");
	     map.put("Ghana", "GHA");
	     map.put("Gibraltar", "GI");
	     map.put("Greenland", "GRL");
	     map.put("Gambia", "GMB");
	     map.put("Guinea", "GIN");
	     map.put("Guadeloupe (French)", "GP");
	     map.put("Equatorial Guinea", "GNQ");
	     map.put("Greece", "GRC");
	     map.put("S. Georgia & S. Sandwich Isls.", "GS");
	     map.put("Guatemala", "GTM");
	     map.put("Guam (USA)", "GU");
	     map.put("Guinea Bissau", "GNB");
	     map.put("Guyana", "GUY");
	     map.put("Hong Kong", "HK");
	     map.put("Heard And McDonald Islands", "HM");
	     map.put("Honduras", "HND");
	     map.put("Croatia", "HRV");
	     map.put("Haiti", "HTI");
	     map.put("Hungary", "HUN");
	     map.put("Indonesia", "IDN");
	     map.put("Ireland", "IRL");
	     map.put("Israel", "ISR");
	     map.put("India", "IND");
	     map.put("British Indian Ocean Territory", "IO");
	     map.put("Iraq", "IRQ");
	     map.put("Iran", "IRN");
	     map.put("Iceland", "ISL");
	     map.put("Italy", "ITA");
	     map.put("Jamaica", "JAM");
	     map.put("Jordan", "JOR");
	     map.put("Japan", "JPN");
	     map.put("Kosovo", "CS-KM");
	     map.put("Kenya", "KEN");
	     map.put("Kyrgyzstan", "KGZ");
	     map.put("Cambodia", "KHM");
	     map.put("Kiribati", "KI");
	     map.put("Comoros", "KM");
	     map.put("Egypt", "EGY");
	     map.put("Saint Kitts & Nevis Anguilla", "KN");
	     map.put("North Korea", "PRK");
	     map.put("South Korea", "KOR");
	     map.put("Kuwait", "KWT");
	     map.put("South Sudan", "SDS");
	     map.put("Cayman Islands", "KY");
	     map.put("Kazakhstan", "KAZ");
	     map.put("Laos", "LAO");
	     map.put("Lebanon", "LB");
	     map.put("Saint Lucia", "LC");
	     map.put("Liechtenstein", "LI");
	     map.put("Sri Lanka", "LKA");
	     map.put("Liberia", "LBR");
	     map.put("Lesotho", "LS");
	     map.put("Lithuania", "LTU");
	     map.put("Luxembourg", "LUX");
	     map.put("Latvia", "LVA");
	     map.put("Libya", "LBY");
	     map.put("Morocco", "MAR");
	     map.put("Mexico", "MEX");
	     map.put("Monaco", "MC");
	     map.put("Moldavia", "MDA");
	     map.put("Madagascar", "MDG");
	     map.put("Marshall Islands", "MH");
	     map.put("Macedonia", "MKD");
	     map.put("Mali", "MLI");
	     map.put("Myanmar", "MMR");
	     map.put("Mongolia", "MNG");
	     map.put("Macau", "MO");
	     map.put("Northern Mariana Islands", "MP");
	     map.put("Martinique (French)", "MQ");
	     map.put("Mauritania", "MRT");
	     map.put("Montserrat", "MS");
	     map.put("Montenegro", "MNE");
	     map.put("Malta", "MT");
	     map.put("Mauritius", "MU");
	     map.put("Maldives", "MV");
	     map.put("Malawi", "MWI");
	     map.put("Mexico", "MEX");
	     map.put("Malaysia", "MYS");
	     map.put("Mozambique", "MOZ");
	     map.put("Namibia", "NAM");
	     map.put("New Caledonia (French)", "NC");
	     map.put("Niger", "NER");
	     map.put("Norfolk Island", "NF");
	     map.put("Nigeria", "NGA");
	     map.put("Nicaragua", "NIC");
	     map.put("Netherlands", "NLD");
	     map.put("Norway", "NOR");
	     map.put("Nepal", "NPL");
	     map.put("Nauru", "NR");
	     map.put("Neutral Zone", "NT");
	     map.put("Niue", "NU");
	     map.put("New Zealand", "NZL");
	     map.put("Oman", "OMN");
	     map.put("Panama", "PAN");
	     map.put("Peru", "PER");
	     map.put("Polynesia (French)", "PF");
	     map.put("Papua New Guinea", "PNG");
	     map.put("Philippines", "PHL");
	     map.put("Paraguay", "PRY");
	     map.put("Pakistan", "PAK");
	     map.put("Poland", "POL");
	     map.put("Saint Pierre And Miquelon", "PM");
	     map.put("Pitcairn Island", "PN");
	     map.put("Puerto Rico", "PRI");
	     map.put("Portugal", "PRT");
	     map.put("Palau", "PW");
	     map.put("Qatar", "QAT");
	     map.put("Reunion (French)", "RE");
	     map.put("Romania", "ROU");
	     map.put("Russian Federation", "RUS");
	     map.put("Rwanda", "RWA");
	     map.put("Saudi Arabia", "SAU");
	     map.put("Solomon Islands", "SB");
	     map.put("Switzerland", "CHE");
	     map.put("Seychelles", "SC");
	     map.put("Serbia", "SRB");
	     map.put("Sudan", "SD");
	     map.put("Sweden", "SWE");
	     map.put("Spain", "ESP");
	     map.put("Singapore", "SG");
	     map.put("Saint Helena", "SH");
	     map.put("Slovenia", "SVN");
	     map.put("Svalbard And Jan Mayen Islands", "SJ");
	     map.put("Slovakia", "SVK");
	     map.put("Sierra Leone", "SLE");
	     map.put("San Marino", "SM");
	     map.put("Senegal", "SEN");
	     map.put("Somalia", "SOM");
	     map.put("Suriname", "SUR");
	     map.put("Saint Tome (Sao Tome) And Principe", "ST");
	     map.put("Former USSR", "SU");
	     map.put("Sudan", "SDN");
	     map.put("El Salvador", "SLV");
	     map.put("Syria", "SYR");
	     map.put("Swaziland", "SZ");
	     map.put("Turks And Caicos Islands", "TC");
	     map.put("Chad", "TCD");
	     map.put("French Southern Territories", "TF");
	     map.put("Togo", "TGO");
	     map.put("Thailand", "THA");
	     map.put("Tadjikistan", "TJ");
	     map.put("Tokelau", "TK");
	     map.put("Turkmenistan", "TKM");
	     map.put("Tunisia", "TUN");
	     map.put("Tonga", "TO");
	     map.put("East Timor", "TP");
	     map.put("Turkey", "TUR");
	     map.put("Trinidad And Tobago", "TT");
	     map.put("Tuvalu", "TV");
	     map.put("Taiwan", "TW");
	     map.put("Tanzania", "TZA");
	     map.put("Ukraine", "UKR");
	     map.put("Uganda", "UGA");
	     map.put("United Kingdom", "GBR");
	     map.put("USA Minor Outlying Islands", "UM");
	     map.put("United States", "USA");
	     map.put("Uruguay", "URY");
	     map.put("Uzbekistan", "UZB");
	     map.put("Holy See (Vatican City State)", "VA");
	     map.put("Saint Vincent & Grenadines", "VC");
	     map.put("Venezuela", "VEN");
	     map.put("Virgin Islands (British)", "VG");
	     map.put("Virgin Islands (USA)", "VI");
	     map.put("Vietnam", "VNM");
	     map.put("Vanuatu", "VU");
	     map.put("Wallis And Futuna Islands", "WF");
	     map.put("Samoa", "WS");
	     map.put("Yemen", "YEM");
	     map.put("Mayotte", "YT");
	     map.put("Yugoslavia", "YU");
	     map.put("South Africa", "ZAF");
	     map.put("Zambia", "ZMB");
	     map.put("Zaire", "ZR");
	     map.put("Zimbabwe", "ZWE");
	     return map;
	}
	public void invertMap(){
		HashMap<String, String> x = generateCountryCode();
		for(HashMap.Entry<String, String> e : x.entrySet()){
			 countryMap.put(e.getValue(), e.getKey());
		}
	}
}

