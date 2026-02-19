package com.fitx.app.data.repository

import com.fitx.app.domain.model.FoodItem
import kotlin.math.round

object OfflineFoodCatalog {
    val items: List<FoodItem> by lazy {
        (baseItems + generatePortionVariants() + generateAliasItems())
            .distinctBy { normalizeName(it.name) }
            .sortedBy { it.name }
    }

    val totalItems: Int
        get() = items.size

    private val baseItems: List<FoodItem> = listOf(
        FoodItem(9_100_001, "Egg, whole (1 large)", 78.0, 6.3, 0.6, 5.3),
        FoodItem(9_100_002, "Egg white (1 large)", 17.0, 3.6, 0.2, 0.1),
        FoodItem(9_100_003, "Egg yolk (1 large)", 55.0, 2.7, 0.6, 4.5),
        FoodItem(9_100_004, "Chicken breast, cooked (100g)", 165.0, 31.0, 0.0, 3.6),
        FoodItem(9_100_005, "Chicken thigh, cooked (100g)", 209.0, 26.0, 0.0, 10.9),
        FoodItem(9_100_006, "Chicken mince, lean (100g)", 173.0, 27.0, 0.0, 7.0),
        FoodItem(9_100_007, "Turkey breast, cooked (100g)", 135.0, 30.0, 0.0, 1.0),
        FoodItem(9_100_008, "Beef, lean cooked (100g)", 217.0, 26.0, 0.0, 12.0),
        FoodItem(9_100_009, "Mutton, cooked (100g)", 294.0, 25.0, 0.0, 21.0),
        FoodItem(9_100_010, "Pork loin, cooked (100g)", 242.0, 27.0, 0.0, 14.0),
        FoodItem(9_100_011, "Salmon, cooked (100g)", 206.0, 22.0, 0.0, 12.0),
        FoodItem(9_100_012, "Tuna, canned in water (100g)", 116.0, 26.0, 0.0, 1.0),
        FoodItem(9_100_013, "Tilapia, cooked (100g)", 129.0, 26.0, 0.0, 2.7),
        FoodItem(9_100_014, "Sardines, canned (100g)", 208.0, 24.0, 0.0, 11.0),
        FoodItem(9_100_015, "Mackerel, cooked (100g)", 262.0, 24.0, 0.0, 17.0),
        FoodItem(9_100_016, "Shrimp, cooked (100g)", 99.0, 24.0, 0.2, 0.3),
        FoodItem(9_100_017, "Prawns, cooked (100g)", 105.0, 24.0, 0.0, 1.0),
        FoodItem(9_100_018, "Crab, cooked (100g)", 97.0, 21.0, 0.0, 1.5),
        FoodItem(9_100_019, "Paneer (100g)", 265.0, 18.0, 3.0, 20.0),
        FoodItem(9_100_020, "Tofu, firm (100g)", 76.0, 8.0, 1.9, 4.8),
        FoodItem(9_100_021, "Tempeh (100g)", 192.0, 20.0, 8.0, 11.0),
        FoodItem(9_100_022, "Soy chunks, dry (50g)", 172.0, 26.0, 14.0, 0.3),
        FoodItem(9_100_023, "Lentils, cooked (1 cup)", 230.0, 18.0, 40.0, 0.8),
        FoodItem(9_100_024, "Chickpeas, cooked (1 cup)", 269.0, 14.5, 45.0, 4.2),
        FoodItem(9_100_025, "Kidney beans, cooked (1 cup)", 225.0, 15.0, 40.0, 0.9),
        FoodItem(9_100_026, "Black beans, cooked (1 cup)", 227.0, 15.2, 41.0, 0.9),
        FoodItem(9_100_027, "Green peas, cooked (100g)", 84.0, 5.4, 15.0, 0.4),
        FoodItem(9_100_028, "Whey protein (1 scoop)", 120.0, 24.0, 3.0, 1.5),
        FoodItem(9_100_029, "Casein protein (1 scoop)", 110.0, 24.0, 3.0, 1.0),
        FoodItem(9_100_030, "Greek yogurt, plain (170g)", 100.0, 17.0, 6.0, 0.0),
        FoodItem(9_100_031, "Cottage cheese, low-fat (100g)", 98.0, 11.0, 3.4, 4.3),

        FoodItem(9_100_032, "Rice, white cooked (100g)", 130.0, 2.7, 28.0, 0.3),
        FoodItem(9_100_033, "Rice, brown cooked (100g)", 111.0, 2.6, 23.0, 0.9),
        FoodItem(9_100_034, "Rice, basmati cooked (100g)", 121.0, 3.5, 25.0, 0.4),
        FoodItem(9_100_035, "Quinoa, cooked (100g)", 120.0, 4.4, 21.3, 1.9),
        FoodItem(9_100_036, "Oats, dry (40g)", 156.0, 6.8, 26.5, 3.2),
        FoodItem(9_100_037, "Oatmeal, cooked (1 cup)", 154.0, 6.0, 27.0, 3.0),
        FoodItem(9_100_038, "Poha, cooked (1 cup)", 180.0, 3.5, 32.0, 4.0),
        FoodItem(9_100_039, "Upma, cooked (1 cup)", 192.0, 5.0, 30.0, 6.0),
        FoodItem(9_100_040, "Idli (1 piece)", 58.0, 2.0, 12.0, 0.4),
        FoodItem(9_100_041, "Dosa, plain (1 piece)", 168.0, 4.0, 26.0, 5.0),
        FoodItem(9_100_042, "Uttapam, plain (1 piece)", 210.0, 6.0, 34.0, 6.0),
        FoodItem(9_100_043, "Roti/Chapati (1 medium)", 120.0, 3.5, 20.0, 3.0),
        FoodItem(9_100_044, "Paratha, plain (1 medium)", 260.0, 5.0, 30.0, 12.0),
        FoodItem(9_100_045, "Whole wheat bread (1 slice)", 69.0, 3.6, 12.0, 1.1),
        FoodItem(9_100_046, "Multigrain bread (1 slice)", 75.0, 3.8, 13.0, 1.3),
        FoodItem(9_100_047, "White bread (1 slice)", 67.0, 2.0, 13.0, 0.9),
        FoodItem(9_100_048, "Pasta, cooked (100g)", 157.0, 5.8, 31.0, 0.9),
        FoodItem(9_100_049, "Whole wheat pasta, cooked (100g)", 149.0, 5.5, 30.0, 1.0),
        FoodItem(9_100_050, "Noodles, cooked (100g)", 138.0, 4.5, 25.0, 2.1),
        FoodItem(9_100_051, "Vermicelli, cooked (1 cup)", 221.0, 6.0, 42.0, 3.0),
        FoodItem(9_100_052, "Sweet potato, boiled (100g)", 86.0, 1.6, 20.0, 0.1),
        FoodItem(9_100_053, "Potato, boiled (100g)", 87.0, 1.9, 20.0, 0.1),
        FoodItem(9_100_054, "Corn, boiled (100g)", 96.0, 3.4, 21.0, 1.5),
        FoodItem(9_100_055, "Muesli (50g)", 190.0, 6.0, 32.0, 4.5),
        FoodItem(9_100_056, "Granola (50g)", 235.0, 5.0, 32.0, 9.0),
        FoodItem(9_100_057, "Khichdi (1 cup)", 220.0, 8.0, 38.0, 4.0),
        FoodItem(9_100_058, "Biryani, chicken (1 cup)", 320.0, 15.0, 42.0, 10.0),
        FoodItem(9_100_059, "Sambar (1 cup)", 96.0, 4.0, 14.0, 2.0),
        FoodItem(9_100_060, "Dal tadka (1 cup)", 210.0, 10.0, 28.0, 6.0),

        FoodItem(9_100_061, "Banana (1 medium)", 105.0, 1.3, 27.0, 0.3),
        FoodItem(9_100_062, "Apple (1 medium)", 95.0, 0.5, 25.0, 0.3),
        FoodItem(9_100_063, "Orange (1 medium)", 62.0, 1.2, 15.0, 0.2),
        FoodItem(9_100_064, "Mango (100g)", 60.0, 0.8, 15.0, 0.4),
        FoodItem(9_100_065, "Papaya (100g)", 43.0, 0.5, 11.0, 0.3),
        FoodItem(9_100_066, "Pineapple (100g)", 50.0, 0.5, 13.0, 0.1),
        FoodItem(9_100_067, "Grapes (100g)", 69.0, 0.7, 18.0, 0.2),
        FoodItem(9_100_068, "Watermelon (100g)", 30.0, 0.6, 8.0, 0.2),
        FoodItem(9_100_069, "Muskmelon (100g)", 34.0, 0.8, 8.0, 0.2),
        FoodItem(9_100_070, "Pear (1 medium)", 101.0, 0.6, 27.0, 0.2),
        FoodItem(9_100_071, "Guava (100g)", 68.0, 2.6, 14.0, 1.0),
        FoodItem(9_100_072, "Pomegranate (100g)", 83.0, 1.7, 19.0, 1.2),
        FoodItem(9_100_073, "Kiwi (1 medium)", 42.0, 0.8, 10.0, 0.4),
        FoodItem(9_100_074, "Strawberries (100g)", 32.0, 0.7, 8.0, 0.3),
        FoodItem(9_100_075, "Blueberries (100g)", 57.0, 0.7, 14.0, 0.3),
        FoodItem(9_100_076, "Raspberries (100g)", 52.0, 1.2, 12.0, 0.7),
        FoodItem(9_100_077, "Dates (3 pieces)", 199.0, 1.8, 54.0, 0.1),
        FoodItem(9_100_078, "Raisins (30g)", 90.0, 0.9, 24.0, 0.1),
        FoodItem(9_100_079, "Dry figs (2 pieces)", 94.0, 1.3, 24.0, 0.4),
        FoodItem(9_100_080, "Tender coconut water (250ml)", 45.0, 1.7, 9.0, 0.2),

        FoodItem(9_100_081, "Broccoli, cooked (100g)", 35.0, 2.4, 7.0, 0.4),
        FoodItem(9_100_082, "Spinach, cooked (100g)", 23.0, 3.0, 3.8, 0.3),
        FoodItem(9_100_083, "Kale, cooked (100g)", 36.0, 2.5, 7.3, 0.5),
        FoodItem(9_100_084, "Lettuce (100g)", 15.0, 1.4, 2.9, 0.2),
        FoodItem(9_100_085, "Cucumber (100g)", 16.0, 0.7, 3.6, 0.1),
        FoodItem(9_100_086, "Tomato (100g)", 18.0, 0.9, 3.9, 0.2),
        FoodItem(9_100_087, "Carrot (100g)", 41.0, 0.9, 10.0, 0.2),
        FoodItem(9_100_088, "Beetroot, boiled (100g)", 44.0, 1.7, 10.0, 0.2),
        FoodItem(9_100_089, "Cauliflower, cooked (100g)", 23.0, 1.8, 4.1, 0.5),
        FoodItem(9_100_090, "Cabbage, cooked (100g)", 23.0, 1.3, 5.5, 0.1),
        FoodItem(9_100_091, "Bell pepper (100g)", 31.0, 1.0, 6.0, 0.3),
        FoodItem(9_100_092, "Onion (100g)", 40.0, 1.1, 9.0, 0.1),
        FoodItem(9_100_093, "Zucchini, cooked (100g)", 17.0, 1.2, 3.1, 0.3),
        FoodItem(9_100_094, "Mushroom, cooked (100g)", 28.0, 3.6, 4.0, 0.5),
        FoodItem(9_100_095, "Okra, cooked (100g)", 33.0, 2.0, 7.0, 0.2),
        FoodItem(9_100_096, "Eggplant, cooked (100g)", 35.0, 0.8, 9.0, 0.2),
        FoodItem(9_100_097, "Pumpkin, cooked (100g)", 20.0, 0.7, 4.9, 0.1),
        FoodItem(9_100_098, "Bottle gourd, cooked (100g)", 15.0, 0.6, 3.4, 0.1),
        FoodItem(9_100_099, "Ridge gourd, cooked (100g)", 18.0, 0.7, 4.0, 0.1),
        FoodItem(9_100_100, "Mixed vegetable curry (1 cup)", 150.0, 4.0, 16.0, 8.0),

        FoodItem(9_100_101, "Milk, whole (250ml)", 152.0, 8.0, 12.0, 8.0),
        FoodItem(9_100_102, "Milk, low-fat (250ml)", 103.0, 8.5, 12.0, 2.5),
        FoodItem(9_100_103, "Milk, skim (250ml)", 83.0, 8.3, 12.0, 0.2),
        FoodItem(9_100_104, "Curd, plain (100g)", 61.0, 3.5, 4.7, 3.3),
        FoodItem(9_100_105, "Yogurt, low-fat (100g)", 63.0, 5.3, 7.0, 1.5),
        FoodItem(9_100_106, "Cheese, cheddar (30g)", 121.0, 7.0, 0.4, 10.0),
        FoodItem(9_100_107, "Cheese, mozzarella (30g)", 85.0, 6.0, 1.0, 6.0),
        FoodItem(9_100_108, "Butter (1 tbsp)", 102.0, 0.1, 0.0, 11.5),
        FoodItem(9_100_109, "Ghee (1 tbsp)", 120.0, 0.0, 0.0, 14.0),
        FoodItem(9_100_110, "Olive oil (1 tbsp)", 119.0, 0.0, 0.0, 13.5),
        FoodItem(9_100_111, "Coconut oil (1 tbsp)", 117.0, 0.0, 0.0, 13.6),
        FoodItem(9_100_112, "Peanut butter (1 tbsp)", 94.0, 3.6, 3.2, 8.0),
        FoodItem(9_100_113, "Almonds (28g)", 164.0, 6.0, 6.0, 14.0),
        FoodItem(9_100_114, "Cashews (28g)", 157.0, 5.0, 9.0, 12.0),
        FoodItem(9_100_115, "Walnuts (28g)", 185.0, 4.3, 3.9, 18.5),
        FoodItem(9_100_116, "Pistachios (28g)", 159.0, 5.7, 8.0, 12.8),
        FoodItem(9_100_117, "Peanuts, roasted (28g)", 166.0, 7.0, 6.0, 14.0),
        FoodItem(9_100_118, "Chia seeds (1 tbsp)", 58.0, 2.0, 5.0, 4.0),
        FoodItem(9_100_119, "Flax seeds (1 tbsp)", 55.0, 1.9, 3.0, 4.3),
        FoodItem(9_100_120, "Sunflower seeds (28g)", 164.0, 5.5, 6.0, 14.0),
        FoodItem(9_100_121, "Pumpkin seeds (28g)", 151.0, 8.0, 4.0, 13.0),
        FoodItem(9_100_122, "Dark chocolate (20g)", 120.0, 1.5, 9.0, 8.5),
        FoodItem(9_100_123, "Biscuit (2 pieces)", 140.0, 2.0, 20.0, 6.0),
        FoodItem(9_100_124, "Samosa (1 medium)", 250.0, 5.0, 30.0, 12.0),
        FoodItem(9_100_125, "Medu vada (1 piece)", 150.0, 3.0, 16.0, 8.0),
        FoodItem(9_100_126, "Pakora (100g)", 280.0, 6.0, 24.0, 18.0),
        FoodItem(9_100_127, "French fries (100g)", 312.0, 3.4, 41.0, 15.0),
        FoodItem(9_100_128, "Popcorn, air popped (3 cups)", 93.0, 3.0, 19.0, 1.0),
        FoodItem(9_100_129, "Protein bar (1 bar)", 220.0, 20.0, 22.0, 7.0),
        FoodItem(9_100_130, "Black coffee (1 cup)", 2.0, 0.3, 0.0, 0.0),
        FoodItem(9_100_131, "Tea with milk and sugar (1 cup)", 80.0, 2.0, 12.0, 2.5),
        FoodItem(9_100_132, "Orange juice (250ml)", 112.0, 1.7, 26.0, 0.5),
        FoodItem(9_100_133, "Cola soft drink (330ml)", 139.0, 0.0, 35.0, 0.0),
        FoodItem(9_100_134, "Lassi, sweet (250ml)", 180.0, 6.0, 28.0, 5.0),
        FoodItem(9_100_135, "Banana milk smoothie (300ml)", 210.0, 8.0, 35.0, 4.0),
        FoodItem(9_100_136, "Peanut chikki (25g)", 130.0, 3.5, 12.0, 8.0),
        FoodItem(9_100_137, "Protein oats shake (300ml)", 260.0, 24.0, 28.0, 6.0),
        FoodItem(9_100_138, "Chicken sandwich (1)", 320.0, 23.0, 31.0, 11.0),
        FoodItem(9_100_139, "Veg sandwich (1)", 260.0, 8.0, 36.0, 9.0),
        FoodItem(9_100_140, "Paneer roll (1)", 340.0, 14.0, 35.0, 16.0),
        FoodItem(9_100_141, "Chicken roll (1)", 360.0, 20.0, 34.0, 17.0),
        FoodItem(9_100_142, "Omelette, 2 eggs", 180.0, 13.0, 2.0, 13.0),
        FoodItem(9_100_143, "Scrambled eggs, 2 eggs", 190.0, 12.0, 2.0, 14.0),
        FoodItem(9_100_144, "Boiled egg, 2 eggs", 156.0, 12.6, 1.2, 10.6),
        FoodItem(9_100_145, "Rajma curry (1 cup)", 230.0, 11.0, 34.0, 6.0),
        FoodItem(9_100_146, "Chole curry (1 cup)", 260.0, 12.0, 32.0, 9.0),
        FoodItem(9_100_147, "Palak paneer (1 cup)", 280.0, 14.0, 10.0, 20.0),
        FoodItem(9_100_148, "Paneer bhurji (1 cup)", 300.0, 18.0, 9.0, 21.0),
        FoodItem(9_100_149, "Chicken curry (1 cup)", 290.0, 22.0, 8.0, 19.0),
        FoodItem(9_100_150, "Fish curry (1 cup)", 240.0, 20.0, 7.0, 14.0),
        FoodItem(9_100_151, "Avocado (100g)", 160.0, 2.0, 9.0, 15.0),
        FoodItem(9_100_152, "Hummus (2 tbsp)", 70.0, 2.0, 4.0, 5.0),
        FoodItem(9_100_153, "Peanut chutney (2 tbsp)", 95.0, 3.0, 4.0, 7.0),
        FoodItem(9_100_154, "Coconut chutney (2 tbsp)", 100.0, 1.0, 4.0, 9.0),
        FoodItem(9_100_155, "Tomato chutney (2 tbsp)", 35.0, 1.0, 5.0, 1.0),
        FoodItem(9_100_156, "Sprouts salad (1 cup)", 120.0, 9.0, 18.0, 2.0),
        FoodItem(9_100_157, "Fruit salad (1 cup)", 110.0, 1.5, 27.0, 0.4),
        FoodItem(9_100_158, "Boiled chickpea salad (1 cup)", 220.0, 11.0, 34.0, 4.0),
        FoodItem(9_100_159, "Masala oats (1 cup)", 190.0, 6.0, 30.0, 5.0),
        FoodItem(9_100_160, "Peanut butter toast (1 slice)", 180.0, 7.0, 16.0, 10.0)
    )

    private data class PortionRule(
        val marker: String,
        val replacements: List<Pair<String, Double>>
    )

    private val portionRules = listOf(
        PortionRule("(100g)", listOf("(50g)" to 0.5, "(150g)" to 1.5, "(200g)" to 2.0)),
        PortionRule("(1 cup)", listOf("(1/2 cup)" to 0.5, "(2 cups)" to 2.0)),
        PortionRule("(1 tbsp)", listOf("(2 tbsp)" to 2.0)),
        PortionRule("(1 medium)", listOf("(2 medium)" to 2.0)),
        PortionRule("(1 slice)", listOf("(2 slices)" to 2.0)),
        PortionRule("(1 piece)", listOf("(2 pieces)" to 2.0)),
        PortionRule("(250ml)", listOf("(500ml)" to 2.0)),
        PortionRule("(300ml)", listOf("(150ml)" to 0.5))
    )

    private val aliases = listOf(
        "boiled egg" to "Boiled egg, 2 eggs",
        "egg boiled" to "Boiled egg, 2 eggs",
        "egg white boiled" to "Egg white (1 large)",
        "fried egg" to "Egg, whole (1 large)",
        "grilled chicken" to "Chicken breast, cooked (100g)",
        "chicken grilled" to "Chicken breast, cooked (100g)",
        "chicken breast" to "Chicken breast, cooked (100g)",
        "chicken thigh" to "Chicken thigh, cooked (100g)",
        "chicken curry indian" to "Chicken curry (1 cup)",
        "fish fry" to "Tilapia, cooked (100g)",
        "fish grilled" to "Salmon, cooked (100g)",
        "prawn fry" to "Prawns, cooked (100g)",
        "paneer tikka" to "Paneer (100g)",
        "paneer curry" to "Palak paneer (1 cup)",
        "paneer bhurji indian" to "Paneer bhurji (1 cup)",
        "tofu stir fry" to "Tofu, firm (100g)",
        "dal" to "Dal tadka (1 cup)",
        "dal fry" to "Dal tadka (1 cup)",
        "rajma" to "Rajma curry (1 cup)",
        "chole" to "Chole curry (1 cup)",
        "chickpea curry" to "Chole curry (1 cup)",
        "sprouts" to "Sprouts salad (1 cup)",
        "rice white" to "Rice, white cooked (100g)",
        "plain rice" to "Rice, white cooked (100g)",
        "rice brown" to "Rice, brown cooked (100g)",
        "basmati rice" to "Rice, basmati cooked (100g)",
        "jeera rice" to "Rice, white cooked (100g)",
        "oatmeal" to "Oatmeal, cooked (1 cup)",
        "overnight oats" to "Oats, dry (40g)",
        "masala oats packet" to "Masala oats (1 cup)",
        "chapati" to "Roti/Chapati (1 medium)",
        "phulka" to "Roti/Chapati (1 medium)",
        "whole wheat roti" to "Roti/Chapati (1 medium)",
        "parotta" to "Paratha, plain (1 medium)",
        "poha" to "Poha, cooked (1 cup)",
        "upma" to "Upma, cooked (1 cup)",
        "idly" to "Idli (1 piece)",
        "plain dosa" to "Dosa, plain (1 piece)",
        "uttappa" to "Uttapam, plain (1 piece)",
        "khichdi" to "Khichdi (1 cup)",
        "veg biryani" to "Biryani, chicken (1 cup)",
        "chicken biryani" to "Biryani, chicken (1 cup)",
        "sweet potato boiled" to "Sweet potato, boiled (100g)",
        "potato boiled" to "Potato, boiled (100g)",
        "banana" to "Banana (1 medium)",
        "apple fruit" to "Apple (1 medium)",
        "orange fruit" to "Orange (1 medium)",
        "mango fruit" to "Mango (100g)",
        "papaya fruit" to "Papaya (100g)",
        "watermelon fruit" to "Watermelon (100g)",
        "strawberry fruit" to "Strawberries (100g)",
        "blueberry fruit" to "Blueberries (100g)",
        "avocado fruit" to "Avocado (100g)",
        "salad" to "Fruit salad (1 cup)",
        "veg salad" to "Sprouts salad (1 cup)",
        "brocolli" to "Broccoli, cooked (100g)",
        "capsicum" to "Bell pepper (100g)",
        "mushrooms" to "Mushroom, cooked (100g)",
        "milk full cream" to "Milk, whole (250ml)",
        "milk toned" to "Milk, low-fat (250ml)",
        "curd" to "Curd, plain (100g)",
        "dahi" to "Curd, plain (100g)",
        "yoghurt" to "Yogurt, low-fat (100g)",
        "ghee spoon" to "Ghee (1 tbsp)",
        "olive oil spoon" to "Olive oil (1 tbsp)",
        "coconut oil spoon" to "Coconut oil (1 tbsp)",
        "peanut butter" to "Peanut butter (1 tbsp)",
        "almond nuts" to "Almonds (28g)",
        "cashew nuts" to "Cashews (28g)",
        "walnut nuts" to "Walnuts (28g)",
        "pista" to "Pistachios (28g)",
        "chia" to "Chia seeds (1 tbsp)",
        "flax" to "Flax seeds (1 tbsp)",
        "sunflower seed" to "Sunflower seeds (28g)",
        "pumpkin seed" to "Pumpkin seeds (28g)",
        "protein shake" to "Protein oats shake (300ml)",
        "whey shake" to "Whey protein (1 scoop)",
        "protein bar" to "Protein bar (1 bar)",
        "black coffee" to "Black coffee (1 cup)",
        "tea milk sugar" to "Tea with milk and sugar (1 cup)",
        "orange juice fresh" to "Orange juice (250ml)",
        "lassi sweet" to "Lassi, sweet (250ml)",
        "banana smoothie" to "Banana milk smoothie (300ml)",
        "sandwich chicken" to "Chicken sandwich (1)",
        "sandwich veg" to "Veg sandwich (1)",
        "paneer wrap" to "Paneer roll (1)",
        "chicken wrap" to "Chicken roll (1)",
        "hummus dip" to "Hummus (2 tbsp)",
        "chutney coconut" to "Coconut chutney (2 tbsp)",
        "chutney tomato" to "Tomato chutney (2 tbsp)",
        "chutney peanut" to "Peanut chutney (2 tbsp)"
    )

    private fun generatePortionVariants(): List<FoodItem> {
        var nextId = 9_200_001L
        return baseItems.flatMap { base ->
            val rule = portionRules.firstOrNull { base.name.contains(it.marker) } ?: return@flatMap emptyList()
            rule.replacements.map { (replacement, scale) ->
                scaledItem(
                    id = nextId++,
                    name = base.name.replace(rule.marker, replacement),
                    base = base,
                    scale = scale
                )
            }
        }
    }

    private fun generateAliasItems(): List<FoodItem> {
        var nextId = 9_300_001L
        return aliases.mapNotNull { (aliasName, canonicalName) ->
            val base = baseItems.firstOrNull { item ->
                item.name.equals(canonicalName, ignoreCase = true)
            } ?: return@mapNotNull null
            scaledItem(
                id = nextId++,
                name = aliasName,
                base = base,
                scale = 1.0
            )
        }
    }

    private fun scaledItem(id: Long, name: String, base: FoodItem, scale: Double): FoodItem {
        return FoodItem(
            fdcId = id,
            name = name,
            calories = (base.calories * scale).roundToOneDecimal(),
            protein = (base.protein * scale).roundToOneDecimal(),
            carbs = (base.carbs * scale).roundToOneDecimal(),
            fat = (base.fat * scale).roundToOneDecimal()
        )
    }

    private fun Double.roundToOneDecimal(): Double = round(this * 10.0) / 10.0

    private fun normalizeName(value: String): String {
        return value.trim().lowercase()
    }
}
