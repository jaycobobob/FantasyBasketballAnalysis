package playerDependencies;

public class IndividualStat {
	private String statName;
	private float value;

	public IndividualStat(String statName) {
		this.statName = statName;
		value = 0.0f;
	}

	public IndividualStat(String statName, float value) {
		this.statName = statName;
		this.value = value;
	}

	public String getStatName() {
		return statName;
	}

	public float getValue() {
		return value;
	}

	public String toString() {
		return statName + ": " + Float.toString(value);
	}

}
