enum class Area(
    val dimension: Int,
    override val displayName: String,
    override val cost: Currency?,
): Describe, Purchasable {
    WindowSill(
        dimension = 1,
        displayName = "Window Sill",
        cost = null,
    ),
    Bedroom(
        dimension = 2,
        displayName = "Bedroom",
        cost = Currency(2_000),
    ),
    SpareRoom(
        dimension = 4,
        displayName = "Spare Room",
        cost = Currency(6_000)
    ),
    Apartment(
        dimension = 8,
        displayName = "Apartment",
        cost = Currency(30_000)
    ),
    Warehouse(
        dimension = 16,
        displayName = "Warehouse",
        cost = Currency(250_000),
    );

    val total = dimension * dimension
}

interface Describe {
    val displayName: String
}

interface Purchasable {
    val cost: Currency?
}

enum class Light(
    val strength: Int,
    val joulesPerTick: Int,
    override val displayName: String,
    override val cost: Currency?,
):  Describe, Purchasable {
    Ambient(
        strength = 1,
        joulesPerTick = 0,
        displayName = "Ambient Light",
        cost = null,
    ),
    CFL(
        strength = 2,
        joulesPerTick = 1,
        displayName = "CFL",
        cost = Currency(1_000L),
    ),
    Halogen(
        strength = 5,
        joulesPerTick = 10,
        displayName = "Halogen",
        cost = Currency(100_000L),
    ),
    LED(
        strength = 5,
        joulesPerTick = 1,
        displayName = "LED",
        cost = Currency(1_200_000L),
    )
}

enum class Medium(
    val effectiveness: Int,
    val litresPerTick: Int,
    override val displayName: String,
    override val cost: Currency?,
): Describe, Purchasable {
    Soil(
        effectiveness = 1,
        litresPerTick = 1,
        displayName = "Soil",
        cost = null // TODO make it so you have to buy everything
    ),
    SoilPerlite(
        effectiveness = 2,
        litresPerTick = 1,
        displayName = "Soil + Perlite",
        cost = Currency(1_000),
    ),
    Hydroponics(
        effectiveness = 4,
        litresPerTick = 1,
        displayName = "Hydroponics",
        cost = Currency(100_000),
    )
}

enum class Costs(val cost: Int) {
    ElectricityJoule(cost = 20),
    WaterLitre(cost = 10)
}

data class Currency(var total: Long)