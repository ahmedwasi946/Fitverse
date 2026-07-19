package com.fitverse.api.config;

import com.fitverse.api.address.Address;
import com.fitverse.api.address.AddressRepository;
import com.fitverse.api.category.Category;
import com.fitverse.api.category.CategoryRepository;
import com.fitverse.api.product.Product;
import com.fitverse.api.product.ProductRepository;
import com.fitverse.api.user.Role;
import com.fitverse.api.user.User;
import com.fitverse.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Fills the database with the same placeholder catalog the Phase 1 frontend
 * ships with (see {@code js/data.js}), so the two line up when Phase 4 wires
 * them together. Runs on every startup but only actually inserts anything
 * the first time — unlike Phase 2's in-memory version, this data now
 * survives restarts, so re-seeding into an already-populated database would
 * just create duplicates.
 */
@Component
@RequiredArgsConstructor
@Transactional
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return; // already seeded on a previous startup
        }
        Map<String, Category> categories = seedCategories();
        seedProducts(categories);
        seedUsersAndAddresses();
    }

    private Map<String, Category> seedCategories() {
        List<Category> categories = List.of(
                Category.builder().name("Women").slug("women").description("Women's ready-to-wear").build(),
                Category.builder().name("Men").slug("men").description("Men's ready-to-wear").build(),
                Category.builder().name("Outerwear").slug("outerwear").description("Coats, jackets and layers").build(),
                Category.builder().name("Sale").slug("sale").description("Reduced pieces").build()
        );
        List<Category> saved = categoryRepository.saveAll(categories);
        return saved.stream().collect(java.util.stream.Collectors.toMap(Category::getSlug, c -> c));
    }

    private void seedProducts(Map<String, Category> categories) {
        List<String> allSizes = List.of("XS", "S", "M", "L", "XL");
        String material = "Premium-grade · ethically sourced";
        String shipping = "Free over $150";
        String returns = "30-day complimentary";

        record Seed(String name, String brand, String description, BigDecimal price, BigDecimal salePrice,
                    String category, String imageSeed, int fitConfidence, int stock, String care) {
        }

        List<Seed> seeds = List.of(
                new Seed("Eclipse Shift Dress", "House of Vela",
                        "An open-lace shift cut for movement, worn over a bias slip so the pattern reads without exposing.",
                        new BigDecimal("285.00"), null, "women", "eclipse-shift-dress", 94, 24, "Hand wash cold"),
                new Seed("Nightfall Pleated Trouser", "Atelier Nord",
                        "A tapered trouser with double front pleats and a soft break at the ankle.",
                        new BigDecimal("224.00"), null, "men", "nightfall-pleated-trouser", 91, 30, "Machine wash cold"),
                new Seed("Smoke Sheer Button-Up", "Atelier Nord",
                        "A micro-check button-up in a sheer-weight cotton, cut with a slightly relaxed body.",
                        new BigDecimal("215.00"), null, "men", "smoke-sheer-buttonup", 89, 18, "Dry clean only"),
                new Seed("Quartz Cropped Cardigan", "Maison Lune",
                        "A cropped cardigan knit from a brushed quartz-fleck yarn.",
                        new BigDecimal("124.00"), null, "women", "quartz-cropped-cardigan", 88, 40, "Hand wash cold"),
                new Seed("Lacquer Trench Coat", "Rue Noire",
                        "High-shine lacquered cotton trench. Storm flap and belt.",
                        new BigDecimal("480.00"), null, "outerwear", "lacquer-trench-coat", 92, 12, "Dry clean only"),
                new Seed("Halo Cropped Tank", "Maison Lune",
                        "A cropped tank in heavyweight cotton jersey, screen-printed with the wordmark.",
                        new BigDecimal("58.00"), new BigDecimal("42.00"), "sale", "halo-cropped-tank", 90, 50, "Machine wash cold"),
                new Seed("Storm Quilted Liner", "Rue Noire",
                        "A packable quilted liner built to zip beneath the Lacquer Trench Coat.",
                        new BigDecimal("195.00"), null, "outerwear", "storm-quilted-liner", 87, 20, "Machine wash cold"),
                new Seed("Velvet Wide-Leg Pant", "House of Vela",
                        "A fluid wide-leg pant in crushed velvet, cut high at the waist.",
                        new BigDecimal("245.00"), null, "women", "velvet-wideleg-pant", 93, 22, "Dry clean only"),
                new Seed("Ashfield Wool Overcoat", "Atelier Nord",
                        "A double-faced wool overcoat with a raised collar and horn buttons.",
                        new BigDecimal("340.00"), null, "outerwear", "ashfield-wool-overcoat", 90, 15, "Dry clean only"),
                new Seed("Dune Linen Shirt", "Maison Lune",
                        "A relaxed linen shirt in a sun-bleached neutral, garment-washed for a lived-in feel.",
                        new BigDecimal("98.00"), new BigDecimal("68.00"), "sale", "dune-linen-shirt", 86, 35, "Machine wash cold")
        );

        for (Seed s : seeds) {
            Product product = Product.builder()
                    .name(s.name()).brand(s.brand()).description(s.description())
                    .price(s.price()).salePrice(s.salePrice())
                    .category(categories.get(s.category()))
                    .sizes(allSizes)
                    .fitConfidence(s.fitConfidence())
                    .stockQuantity(s.stock())
                    .material(material).shippingInfo(shipping).returnsInfo(returns).careInfo(s.care())
                    .createdAt(Instant.now())
                    .build();
            product.addImage("https://picsum.photos/seed/" + s.imageSeed() + "/700/875", 0);
            productRepository.save(product);
        }
    }

    private void seedUsersAndAddresses() {
        User admin = userRepository.save(User.builder()
                .name("Admin")
                .email("admin@fitverse.ai")
                .passwordHash(passwordEncoder.encode("Admin@123"))
                .role(Role.ROLE_ADMIN)
                .createdAt(Instant.now())
                .build());

        User customer = userRepository.save(User.builder()
                .name("Sarah Chen")
                .email("sarah.chen@example.com")
                .passwordHash(passwordEncoder.encode("Customer@123"))
                .role(Role.ROLE_USER)
                .createdAt(Instant.now())
                .build());

        addressRepository.save(Address.builder()
                .user(customer).fullName("Sarah Chen").line1("148 Mercer Street")
                .city("New York").zip("10012").country("United States")
                .build());
    }
}
