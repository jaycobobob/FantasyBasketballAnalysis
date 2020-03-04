package processors;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processors.urlProcessor.InvalidContentsException;

public class jsonProcessor {
	public static void fixYearStats(JSONObject player) {
		JSONObject stats = (JSONObject) player.get("stats");
		stats = (JSONObject) stats.get("regularSeason");
		JSONArray seasons = (JSONArray) stats.get("season");

		for (Object obj : seasons) { // for each season
			JSONObject year = (JSONObject) obj; // this is the object containing the year,
			// stats per team, and total stats (total stats needs to be updated)
			JSONArray teamData = (JSONArray) year.get("teams");

			if (teamData.size() != 1) { // the player has been on multiple teams this season
				int teamCount = 0;
				Map<String, Float> playerData = new HashMap<String, Float>();
				for (Object obj2 : teamData) { // iterating through each team for that year
					JSONObject teamStats = (JSONObject) obj2;
					for (Object obj3 : teamStats.keySet()) { // iterating through every stat
						String stat = (String) obj3;
						if (!stat.equals("teamId")) { // we don't care about the team id for total
							if (!playerData.containsKey(stat)) {
								playerData.put(stat, 0.0f);
							}
							playerData.put(stat, playerData.get(stat) + Float.parseFloat((String) teamStats.get(stat)));
						}
					}
					teamCount++;
				}
				JSONObject total = new JSONObject();
				for (String stat : playerData.keySet()) {
					playerData.put(stat, playerData.get(stat) / teamCount);
					DecimalFormat df = new DecimalFormat("###.##");
					total.put(stat, df.format(playerData.get(stat)));
				}
				year.put("total", total);
			}
		}
	}

	private static JSONObject getStatsObject(JSONObject player) {
		player = (JSONObject) player.get("stats");
		player = (JSONObject) player.get("regularSeason");
		player = (JSONObject) ((JSONArray) player.get("season")).get(0);
		player = (JSONObject) player.get("total");
		return player;
	}

	public static JSONObject getStatsObject(int personId) throws IOException, InvalidContentsException {
		JSONObject player = jsonProcessor.getPlayerStatsJSON(personId);
		return jsonProcessor.getStatsObject(player);
	}

	public static JSONObject getPlayerStatsJSON(int personId) throws IOException, InvalidContentsException {
		// Gets the full url of a player page based on the personId
		String url = (String) urlProcessor.getPlayerPage(Integer.toString(personId));
		// Gets the JSON object containing a player's stats from their player stats page
		JSONObject playerData = urlProcessor.getJSONFromURL(url);
		// These two lines filter out the extraneous information we don't care about,
		// and only return the stats
		playerData = (JSONObject) playerData.get("league");
		playerData = (JSONObject) playerData.get("standard");
		jsonProcessor.fixYearStats(playerData);
		return playerData;
	}
}
