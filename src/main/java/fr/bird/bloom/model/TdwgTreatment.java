/**
 * src.model
 * TdwgTreatment
 */
package fr.bird.bloom.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import fr.bird.bloom.utils.BloomConfig;
import org.geotools.geojson.geom.GeometryJSON;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.*;
/**
 * src.model
 * 
 * TdwgTreatment.java
 * TdwgTreatment
 */
public class TdwgTreatment {

	private String uuid;
	private boolean sucessTdwgTreatment;

	/**
	 * 
	 * Tdwg4 code is retrieved for each coordinates 
	 * 
	 * @return void
	 */
	public void checkIsoTdwgCode(DarwinCore fileDarwinCore){
		//change example : locationID="TDWG:MXS-JA"
		this.setSucessTdwgTreatment(true);
		List<String> idListClean = fileDarwinCore.getIDClean();



		for(int i = 0 ; i< idListClean.size() ; i++){
			String id_ = idListClean.get(i);
			//System.out.println(id_);
			if(!"id_".equals(id_ )){

				float latitude = -1;
				float longitude = -1;
				String iso2 = "error";

				String valueLatitude = fileDarwinCore.getValueFromColumn("decimalLatitude_", id_.replaceAll("\"", ""));
				if(!valueLatitude.equals("error")){
					latitude = Float.parseFloat(valueLatitude.replaceAll("\"", ""));
				}

				String valueLongitude = fileDarwinCore.getValueFromColumn("decimalLongitude_", id_.replaceAll("\"", ""));
				if(!valueLongitude.equals("error")){
					longitude = Float.parseFloat(valueLongitude.replaceAll("\"", ""));
				}

				iso2 = fileDarwinCore.getValueFromColumn("countryCode_", id_.replaceAll("\"", "")).replaceAll("\"", "");

				if(!iso2.equals("error") && latitude != -1 && longitude != -1) {
					GeometryFactory geometryFactory = new GeometryFactory();
					Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
					System.out.println("--------------------------------------------------------------");
					System.out.println("---------------- Check point in TDWG4 code -------------------");
					System.out.print("id : " + id_ + "\tLat : " + latitude + "\tLong : " + longitude + "\tiso2 : " + iso2);
					String tdwg4Code = "";
					tdwg4Code = this.tdwg4ContainedPoint(point, iso2.replaceAll("\"", ""));
					System.out.println("\ttdwg4 : " + tdwg4Code);
					System.out.println("--------------------------------------------------------------");

					Statement statement = null;
					try {
						statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DatabaseTreatment newConnectionSelectID = new DatabaseTreatment(statement);
					String sqlSelectID = "SELECT locationID_ FROM Workflow.Clean_" + this.getUuid() + " WHERE Clean_" + this.getUuid() + ".id_=" + id_ + ";";
					newConnectionSelectID.executeSQLcommand("executeQuery", sqlSelectID);
					List<String> selectIDResults = newConnectionSelectID.getResultatSelect();

					String newLocationID = "";
					if (selectIDResults.size() > 1 && !selectIDResults.get(1).replaceAll("\"", "").isEmpty()) {
						newLocationID = selectIDResults.get(1).replaceAll("\"", "") + ";TDWG=" + tdwg4Code;
					} else {
						newLocationID = "TDWG=" + tdwg4Code;
					}

					String sqlUpdateTDWG = "UPDATE Workflow.Clean_" + this.getUuid() + " SET Clean_" + this.getUuid() + ".locationID_=\"" + newLocationID + "\" WHERE Clean_" + this.getUuid() + ".id_=" + id_ + ";";
					//System.out.println(sqlUpdateTDWG);
					Statement statementUpdateClean = null;
					try {
						statementUpdateClean = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DatabaseTreatment newConnectionUpdateClean = new DatabaseTreatment(statementUpdateClean);
					newConnectionUpdateClean.executeSQLcommand("executeUpdate", sqlUpdateTDWG);

					/*
					for (int j = 0; j < messages.size(); i++) {
						if (messages.get(j).contains("Connection error : ")) {
							this.setSucessTdwgTreatment(false);
						}
					}
					*/
				}
			}
		}

	}

	/**
	 * Find code tdwg4 from coordinates 
	 * 
	 * @param geoPoint
	 * @param iso2
	 * @return String
	 */
	public String tdwg4ContainedPoint(Point geoPoint, String iso2){
		GeometryJSON geometryJSON = new GeometryJSON();
		BufferedReader buff = null;
		try {
			buff = new BufferedReader(new FileReader(BloomConfig.getResourcePath() + "tdwg4.json"));
			String line = null;
			while ((line = buff.readLine()) != null) {
				if(line.contains("\"ISO_Code\": \"" + iso2 + "\"")){
					Pattern pattern = Pattern.compile("\"Level4_cod\": \"([A-Z]{3}-[A-Z]{2})\",");
					Matcher matcher = pattern.matcher(line);
					String tdwg4Code = "";
					while(matcher.find()) {
						tdwg4Code = matcher.group(1);
					}
					MultiPolygon multipolygon = geometryJSON.readMultiPolygon(line.substring(0, line.length()-1));
					if(multipolygon.contains(geoPoint)){

						return tdwg4Code;
					}
				}
			}
			buff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";

	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isSucessTdwgTreatment() {
		return sucessTdwgTreatment;
	}

	public void setSucessTdwgTreatment(boolean sucessTdwgTreatment) {
		this.sucessTdwgTreatment = sucessTdwgTreatment;
	}


}
