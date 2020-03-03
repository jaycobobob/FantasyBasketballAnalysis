package processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Contains methods necessary for processing URLs for use in reading player
 * data. This is an abstract class to be used only as a collection of methods
 * assisting in handing URLs and/or reading JSONs from properly formatted URLs.
 * 
 * @author  Jacob Edwards
 * @version %I%, %G%
 * @since   1.0
 */
public class urlProcessor {

	/**
	 * Obtains the JSON contents of a given URL. This method assumes that the
	 * webpage only contains the contents of a json. If it contains anything else,
	 * it throws an InvalidContentsException.
	 * 
	 * @author                           Jacob Edwards
	 * @version                          %I%, %G%
	 * @param   link                     A string that represents a link to grab the
	 *                                   .json contents of
	 * @return                           JSONObject representing a JSON file
	 *                                   containing whatever information was on the
	 *                                   webpage
	 * @throws  IOException              if the URL cannot be accessed
	 * @throws  InvalidContentsException if the URL containing anything other than a
	 *                                   JSON
	 * @see                              <a href=
	 *                                   "http://data.nba.net/prod/v1/2019/players/203500_profile.json">A
	 *                                   Valid JSON page</a>
	 * @since                            1.0
	 */
	public static JSONObject getJSONFromURL(String link) throws IOException, InvalidContentsException {
		try {
			URL url;

			url = new URL(link);
			URLConnection conn = url.openConnection();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			JSONParser parser = new JSONParser();
			JSONObject a = (JSONObject) parser.parse(br.readLine());

			br.close();
			return a;
		} catch (ParseException e) {
			throw new InvalidContentsException("The given URL does not contain only .json");
		}
	}

	/**
	 * Generates a useable link to an NBA player's JSON stat page. This method takes
	 * in a personId and adds it to the template string in order to get a working
	 * player page. The resulting player page contains a players stats by year in a
	 * JSON file, that can be parsed by {@link urlProcessor.getJSONFromURL}.
	 * 
	 * @author           Jacob Edwards
	 * @version          %I%, %G%
	 * @param   personId the string representation of the desired player's personId.
	 *                   A personId is a numerical identifier unique to each NBA
	 *                   player and can be found on the main NBA player data page.
	 * @return           returns a properly formatted link to a specific NBA
	 *                   player's stat page.
	 * @see              <a href=
	 *                   "http://data.nba.net/10s/prod/v1/2019/players.json">List of
	 *                   player personIds</a>
	 * @since            1.0
	 */
	public static String getPlayerPage(String personId) {
		return "http://data.nba.net/prod/v1/2019/players/" + personId + "_profile.json";
	}

	@SuppressWarnings("serial")
	public static class InvalidContentsException extends Exception {
		public InvalidContentsException(String errorMessage) {
			super(errorMessage);
		}
	}
}
