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
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * src.model
 * 
 * TdwgTreatment.java
 * TdwgTreatment
 */
public class TdwgTreatment {

	private String nbSessionRandom;
	private boolean sucessTdwgTreatment;

	public TdwgTreatment(){

	}

	/**
	 * 
	 * Tdwg4 code is retrieved for each coordinates 
	 * 
	 * @return void
	 */
	public void checkIsoTdwgCode(DarwinCore fileDarwinCore){
		//change example : locationID="TDWG:MXS-JA"
		this.setSucessTdwgTreatment(true);
		fileDarwinCore.associateIdData();
		Map<String, List<String>> idAssoData = fileDarwinCore.getIdAssoData();

		int iLatitude = fileDarwinCore.getIndiceFromTag("decimalLatitude_");
		int iLongitude = fileDarwinCore.getIndiceFromTag("decimalLongitude_");
		int iIso2 = fileDarwinCore.getIndiceFromTag("countryCode_");

		for (String id_ : idAssoData.keySet()) {
			if(!id_ .equals("id_")){
				List<String> listInfos = idAssoData.get(id_);

				float latitude = 0;
				float longitude = 0;
				String iso2 = "";

				latitude = Float.parseFloat(listInfos.get(iLatitude).replace("\"", ""));
				longitude = Float.parseFloat(listInfos.get(iLongitude).replace("\"", ""));
				iso2 = listInfos.get(iIso2);
				GeometryFactory geometryFactory = new GeometryFactory();
				Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
				/*System.out.println("--------------------------------------------------------------");
				System.out.println("---------------- Check point in TDWG4 code -------------------");
				System.out.println("Lat : " + latitude + "\tLong : " + longitude);
				System.out.print("iso2 : " + iso2);*/
				String tdwg4Code = "";
				try {
					tdwg4Code = this.tdwg4ContainedPoint(point, iso2.replaceAll("\"", ""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					this.setSucessTdwgTreatment(false);
					e.printStackTrace();
				}
				//System.out.println("\ttdwg4 : " + tdwg4Code);
				//System.out.println("--------------------------------------------------------------");

				Statement statement = null;
				try {
					statement = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatabaseTreatment newConnectionSelectID = new DatabaseTreatment(statement);
				String sqlSelectID = "SELECT locationID_ FROM Workflow.Clean_" + this.getNbSessionRandom() + " WHERE Clean_" + this.getNbSessionRandom() + ".id_=" + id_ + ";";
				newConnectionSelectID.executeSQLcommand("executeQuery", sqlSelectID);
				List<String> selectIDResults = newConnectionSelectID.getResultatSelect();

				String newLocationID = "";
				if(selectIDResults.size() > 1 && !selectIDResults.get(1).replaceAll("\"", "").isEmpty()){
					newLocationID = selectIDResults.get(1).replaceAll("\"", "") + ";TDWG=" + tdwg4Code;
				}
				else{
					newLocationID = "TDWG=" + tdwg4Code;
				}

				String sqlUpdateTDWG = "UPDATE Workflow.Clean_" + this.getNbSessionRandom() + " SET Clean_" + this.getNbSessionRandom() + ".locationID_=\"" + newLocationID + "\" WHERE Clean_" + this.getNbSessionRandom() + ".id_=" + id_ + ";";

				Statement statementUpdateClean = null;
				try {
					statementUpdateClean = ConnectionDatabase.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatabaseTreatment newConnectionUpdateClean = new DatabaseTreatment(statementUpdateClean);
				List<String> messages = newConnectionUpdateClean.executeSQLcommand("executeUpdate", sqlUpdateTDWG);

				for(int i = 0; i < messages.size(); i++){
					if(messages.get(i).contains("Connection error : ")){
						this.setSucessTdwgTreatment(false);
					}
				}
			}
		}

	}

	/**
	 * Find code tdwg4 from coordinates 
	 * 
	 * @param geoPoint
	 * @param iso2
	 * @throws IOException
	 * @return String
	 */
	public String tdwg4ContainedPoint(Point geoPoint, String iso2) throws IOException{
		GeometryJSON geometryJSON = new GeometryJSON();
		BufferedReader buff = new BufferedReader(new FileReader(BloomConfig.getResourcePath() + "tdwg4.json"));

		try {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			buff.close();
		}
		return "";

	}

	public String getNbSessionRandom() {
		return nbSessionRandom;
	}

	public void setNbSessionRandom(String nbSessionRandom) {
		this.nbSessionRandom = nbSessionRandom;
	}

	public boolean isSucessTdwgTreatment() {
		return sucessTdwgTreatment;
	}

	public void setSucessTdwgTreatment(boolean sucessTdwgTreatment) {
		this.sucessTdwgTreatment = sucessTdwgTreatment;
	}


}
