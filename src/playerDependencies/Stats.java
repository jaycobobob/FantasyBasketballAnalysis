package playerDependencies;

import java.util.Map;

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
	private float[] allStats;

}
