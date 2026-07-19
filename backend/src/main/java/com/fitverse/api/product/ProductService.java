package com.fitverse.api.product;

import com.fitverse.api.category.Category;
import com.fitverse.api.category.CategoryService;
import com.fitverse.api.common.exception.ResourceNotFoundException;
import com.fitverse.api.product.dto.ProductRequest;
import com.fitverse.api.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAll(Long categoryId) {
        List<Product> products = categoryId == null
                ? productRepository.findAll()
                : productRepository.findByCategoryId(categoryId);
        return products.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return toResponse(getEntityById(id));
    }

    @Transactional(readOnly = true)
    public Product getEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));
    }

    public ProductResponse create(ProductRequest request) {
        Category category = categoryService.getEntityById(request.categoryId());
        Product product = Product.builder()
                .name(request.name())
                .brand(request.brand())
                .description(request.description())
                .price(request.price())
                .salePrice(request.salePrice())
                .category(category)
                .sizes(request.sizes())
                .fitConfidence(request.fitConfidence())
                .stockQuantity(request.stockQuantity())
                .material(request.material())
                .shippingInfo(request.shippingInfo())
                .returnsInfo(request.returnsInfo())
                .careInfo(request.careInfo())
                .createdAt(Instant.now())
                .build();
        applyImages(product, request.imageUrls());
        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getEntityById(id);
        Category category = categoryService.getEntityById(request.categoryId());
        product.setName(request.name());
        product.setBrand(request.brand());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setSalePrice(request.salePrice());
        product.setCategory(category);
        product.setSizes(request.sizes());
        product.setFitConfidence(request.fitConfidence());
        product.setStockQuantity(request.stockQuantity());
        product.setMaterial(request.material());
        product.setShippingInfo(request.shippingInfo());
        product.setReturnsInfo(request.returnsInfo());
        product.setCareInfo(request.careInfo());
        product.getImages().clear();
        applyImages(product, request.imageUrls());
        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        getEntityById(id);
        productRepository.deleteById(id);
    }

    private void applyImages(Product product, List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            product.addImage(imageUrls.get(i), i);
        }
    }

    public ProductResponse toResponse(Product p) {
        List<String> imageUrls = p.getImages().stream().map(ProductImage::getUrl).toList();
        return new ProductResponse(p.getId(), p.getName(), p.getBrand(), p.getDescription(),
                p.getPrice(), p.getSalePrice(), p.getCategory().getId(), imageUrls, p.getSizes(),
                p.getFitConfidence(), p.getStockQuantity(), p.getMaterial(), p.getShippingInfo(),
                p.getReturnsInfo(), p.getCareInfo());
    }
}
