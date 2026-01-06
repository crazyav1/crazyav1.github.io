package com.electricity.config;

public record Task11Config(
    BatterySpec battery,
    double exportPriceFactor,
    double gridLimitKW
) {
    public static Task11Config fromDefaults() {
        var battery = new BatterySpec(
            5.0,    // initial SOC kWh (assume 50%)
            0.0,    // min SOC
            10.0,   // max SOC
            5.0,    // charge limit kW
            5.0,    // discharge limit kW
            0.95,   // eta charge
            0.95    // eta discharge
        );

        return new Task11Config(
            battery,
            1.0,    // exportPriceFactor
            5.0                    // gridLimitKW
        );
    }
}
