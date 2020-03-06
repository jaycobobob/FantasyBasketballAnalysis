package playerDependencies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processors.jsonProcessor;
import processors.urlProcessor;
import processors.urlProcessor.InvalidContentsException;

/**
 * Holds a collection of stats for an individual player/team. This class
 * supports individual assignment as well as construction from a JSON containing
 * a player as formatted by NBA.com's player database. See <a href=
 * "http://data.nba.net/prod/v1/2019/players/203500_profile.json">Steven
 * Adams</a> player page for an example.
 * 
 * @author  Jacob Edwards
 * @version %I%, %G%
 * @see     <a href= "http://data.nba.net/10s/prod/v1/2019/players.json">All NBA
 *          players pages</a>
 * @since   1.0
 *
 */
public class Stats {
	private static Map<String, Integer> statMap;
	private float[] statArray;

	/**
	 * Returns or creates the static instance of statMap for the {@link Stats}
	 * class. This method looks to see if the static instance of statMap has already
	 * been created. If it has, returns that instance. Otherwise, creates that
	 * instance. That instance is created by looking at the stats object for a
	 * predesignated player, and then mapping the name of those stats to the order
	 * their appear in.
	 * 
	 * @return                          the instance of statMap which maps the names
	 *                                  of stats to the order that they appear in
	 * @throws IOException              if the player whose stats will be given's
	 *                                  page cannot be accessed
	 * @throws InvalidContentsException if the player's stat-page has been changed
	 *                                  in a way to where their page cannot be
	 *                                  parsed into a json
	 */
	private static Map<String, Integer> getStatMapInstance() throws IOException, InvalidContentsException {
		if (statMap == null) {
			JSONObject player = urlProcessor
					.getJSONFromURL("http://data.nba.net/prod/v1/2019/players/203500_profile.json");
			statMap = new HashMap<>();
			player = (JSONObject) player.get("league");
			player = (JSONObject) player.get("standard");
			player = (JSONObject) player.get("stats");
			player = (JSONObject) player.get("regularSeason");
			player = (JSONObject) ((JSONArray) player.get("season")).get(0);
			player = (JSONObject) player.get("total");

			int i = 0;
			for (Object obj : player.keySet()) {
				statMap.put((String) obj, i);
				i++;
			}
		}
		return statMap;
	}

	/**
	 * Checks if a given stat is a valid stat from the player data. This method
	 * checks to see if a given stat is contained in the statMap, which means the
	 * stat can be found in the statArray, and means that the stat can be found
	 * successfully in the statArray.
	 * 
	 * @param  stat the name of the stat to be checked. Valid stats are typically
	 *              abreviations of common NBA stats. For example, "ppg" represents
	 *              the stat "Points per game." However, some valid stats are not so
	 *              straightforward, such as "td3" representing the number of triple
	 *              doubles.
	 * @return      whether or not the stat exists in the statMap, which means the
	 *              stat is either valid or not.
	 */
	public static boolean isStatValid(String stat) {
		return statMap.containsKey(stat);
	}

	/**
	 * Create Stats object with fields from player associated with given personId.
	 * This constructor creates a Stats object capable of storing stats of an NBA
	 * player. It checks to see if the protocol for mapping the names of stats to
	 * specific stats has been created, and if it has not, it initializes that.
	 * 
	 * @param  personId                 the personId of the player to get stats for.
	 *                                  Each NBA player has a personId given to them
	 *                                  by the NBA player database. For example,
	 *                                  Steven Adams' personId is 203500. The
	 *                                  personId of any given player can be seen on
	 *                                  the JSON page for all players.
	 * @throws IOException              if the given player's stat-page cannot be
	 *                                  accessed, most likely due to a poor internet
	 *                                  connection
	 * @throws InvalidContentsException if the given player's stat-page has been
	 *                                  edited in such a where to where the json
	 *                                  data cannot be parsed properly.
	 * @see                             <a href=
	 *                                  "http://data.nba.net/10s/prod/v1/2019/players.json">All
	 *                                  NBA players pages</a>
	 */
	public Stats(int personId) throws IOException, InvalidContentsException {
		statMap = getStatMapInstance();
		statArray = new float[statMap.size()];
		JSONObject stats = jsonProcessor.getStatsObject(personId);
		for (Object obj : stats.keySet()) {
			int index = statMap.get((String) obj);
			statArray[index] = Float.parseFloat((String) stats.get(obj));
		}
	}

	/**
	 * Finds the value of the specified stat. This method first checks to see if the
	 * specified stat is valid. If it is not, returns 0.0f. If the stat is valid, it
	 * grabs the value of the stat and returns it. Stats are stored as float values,
	 * even though some values are not decimals, for example total steals is not a
	 * decimal value, but is still stored as a float. Each stat is associated with a
	 * string, which is taken from the NBA.com player database.
	 * 
	 * @param  stat which stat to get. If the stat exists in the map of valid stats,
	 *              returns the value of that stat. Else, returns 0.0f.
	 * @return      if the specified string represents a valid stat in the Stats
	 *              object, returns the value of that stat. Otherwise, return 0.0f.
	 */
	public float getStat(String stat) {
		if (this.isStatValid(stat)) {
			return statArray[statMap.get(stat)];
		}
		return 0.0f;
	}

	public int size() {
		return statArray.length;
	}

	public StatsIterator getIterator() {
		return new StatsIterator(this);
	}

	public String toString() {
		String out = "";
		StatsIterator iter = this.getIterator();
		while (iter.hasNext()) {
			out += iter.next() + ", ";
		}
		out = out.substring(0, out.length() - 2);
		return out;
	}

	public class StatsIterator {
		private int cursor;
		private Stats statsObj;
		private List<String> keys;

		protected StatsIterator(Stats statsObj) {
			cursor = 0;
			this.statsObj = statsObj;
			try {
				keys = new ArrayList<String>(Stats.getStatMapInstance().keySet());
			} catch (IOException | InvalidContentsException e) {
				keys = new ArrayList<String>();
			}
		}

		public boolean hasNext() {
			return cursor < statsObj.size() && statsObj.size() != 0;
		}

		public IndividualStat next() {
			String stat = keys.get(cursor);
			IndividualStat out = new IndividualStat(stat, statsObj.getStat(stat));
			cursor++;
			return out;
		}
	}

}
