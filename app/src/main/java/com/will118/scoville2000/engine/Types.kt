import kotlinx.serialization.Serializable

@Serializable
enum class Area(
    val dimension: Int,
    override val displayName: String,
    override val cost: Currency?,
): Describe, Purchasable {
    WindowSill(
        dimension = 1,
        displayName = "Windowsill",
        cost = null,
    ),
    Bedroom(
        dimension = 2,
        displayName = "Bedroom",
        cost = Currency(20_000),
    ),
    SpareRoom(
        dimension = 4,
        displayName = "Spare Room",
        cost = Currency(60_000)
    ),
    Apartment(
        dimension = 8,
        displayName = "Apartment",
        cost = Currency(300_000)
    ),
    Warehouse(
        dimension = 16,
        displayName = "Warehouse",
        cost = Currency(2_500_000),
    );

    val total = dimension * dimension
}

interface Describe {
    val displayName: String
}

interface Purchasable {
    val cost: Currency?
}

@Serializable
enum class Light(
    val strength: Int,
    val joulesPerCostTick: Int,
    override val displayName: String,
    override val cost: Currency?,
):  Describe, Purchasable {
    Ambient(
        strength = 1,
        joulesPerCostTick = 0,
        displayName = "Ambient Light",
        cost = null,
    ),
    CFL(
        strength = 2,
        joulesPerCostTick = 1,
        displayName = "CFL",
        cost = Currency(1_000L),
    ),
    Halogen(
        strength = 5,
        joulesPerCostTick = 10,
        displayName = "Halogen",
        cost = Currency(100_000L),
    ),
    LED(
        strength = 5,
        joulesPerCostTick = 1,
        displayName = "LED",
        cost = Currency(1_200_000L),
    )
}

@Serializable
enum class Medium(
    val effectiveness: Int,
    val litresPerCostTick: Int,
    override val displayName: String,
    override val cost: Currency?,
): Describe, Purchasable {
    Soil(
        effectiveness = 2,
        litresPerCostTick = 1,
        displayName = "Soil",
        cost = null // TODO make it so you have to buy everything
    ),
    SoilPerlite(
        effectiveness = 3,
        litresPerCostTick = 1,
        displayName = "Soil + Perlite",
        cost = Currency(1_000),
    ),
    Hydroponics(
        effectiveness = 5,
        litresPerCostTick = 1,
        displayName = "Hydroponics",
        cost = Currency(100_000),
    )
}

enum class Costs(val cost: Int) {
    ElectricityJoule(cost = 3),
    WaterLitre(cost = 1)
}

@Serializable
data class Currency(val total: Long) {
    override fun toString(): String {
        return "₡${total}"
    }
}