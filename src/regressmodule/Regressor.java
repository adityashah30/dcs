package regressmodule;

public class Regressor {

    private StatsDataExtractor statsExtractor;
    private DataRegressor dataRegressor;

    public Regressor() {
        statsExtractor = new StatsDataExtractor();
        dataRegressor = new DataRegressor();
    }

}
