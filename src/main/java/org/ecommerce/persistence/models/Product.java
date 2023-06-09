package org.ecommerce.persistence.models;

import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.format.annotation.DateTimeFormat;
import org.ecommerce.persistence.constraints.AfterTomorrow;
import org.ecommerce.persistence.constraints.ValidateDateRange;

/**
 *
 * @author sergio
 */
@Entity
@Indexed
@Table(name = "PRODUCTS")
@Inheritance(strategy = InheritanceType.JOINED)
@ValidateDateRange(start = "availableFrom", end = "availableTo", message = "{product.daterange.invalid}")
@AnalyzerDef(name = "customanalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
		@TokenFilterDef(factory = LowerCaseFilterFactory.class),
		@TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
				@Parameter(name = "language", value = "Spanish") }) })
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@DocumentId
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(DataTablesOutput.View.class)
	private Long id;

	@NotBlank(message = "{product.name.notnull}")
	@Size(min = 5, max = 80, message = "{product.name.size}")
	@Column(nullable = false, unique = true, length = 80)
	@JsonView(DataTablesOutput.View.class)
	@Field
	@Analyzer(definition = "customanalyzer")
	private String name;

	@Column(nullable = false, precision = 5, scale = 2)
	@DecimalMax(value = "999.99", inclusive = true, message = "{product.price.max}")
	@DecimalMin(value = "00.00", message = "{product.price.min}")
	@JsonView(DataTablesOutput.View.class)
	private Double price;

	@NotBlank(message = "{product.description.notnull}")
	@Size(min = 30, max = 300, message = "{product.description.size}")
	@Column(nullable = false, length = 300)
	@Field
	@Analyzer(definition = "customanalyzer")
	private String description;

	@NotBlank(message = "{product.shortDescription.notnull}")
	@Size(min = 30, max = 200, message = "{product.shortDescription.size}")
	@Column(nullable = false, length = 200)
	@Field
	@Analyzer(definition = "customanalyzer")
	private String shortDescription;

	@NotNull(message = "{product.availableFrom.notnull}")
	@AfterTomorrow(message = "{product.availableFrom.future}")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(nullable = false)
	private Date availableFrom;

	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(nullable = true)
	private Date availableTo;

	@Enumerated(EnumType.STRING)
	private ConsumerTypeEnum consumerType;

	@OneToMany(mappedBy = "product", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Review> reviews = new HashSet();

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(nullable = false)
	@JsonView(DataTablesOutput.View.class)
	private Date createAt = new Date();

	@Enumerated(EnumType.STRING)
	@JsonView(DataTablesOutput.View.class)
	private ProductStatusEnum status;

	@Lob
	@Column(nullable = true)
	private String completeDesc;

	@OneToMany(mappedBy = "product", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<ProductLine> productLines = new ArrayList();

	@ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private ProductCategory category;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public Date getAvailableFrom() {
		return availableFrom;
	}

	public void setAvailableFrom(Date availableFrom) {
		this.availableFrom = availableFrom;
	}

	public Date getAvailableTo() {
		return availableTo;
	}

	public void setAvailableTo(Date availableTo) {
		this.availableTo = availableTo;
	}

	public ConsumerTypeEnum getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(ConsumerTypeEnum consumerType) {
		this.consumerType = consumerType;
	}

	public Set<Review> getReviews() {
		return reviews;
	}

	public void setReviews(Set<Review> reviews) {
		this.reviews = reviews;
	}

	public void addReview(Review review) {
		if (!reviews.contains(review)) {
			reviews.add(review);
			review.setProduct(this);
		}
	}

	public ProductStatusEnum getStatus() {
		return status;
	}

	public void setStatus(ProductStatusEnum status) {
		this.status = status;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getCompleteDesc() {
		return completeDesc;
	}

	public void setCompleteDesc(String completeDesc) {
		this.completeDesc = completeDesc;
	}

	public List<ProductLine> getProductLines() {
		return productLines;
	}

	public void setProductLines(List<ProductLine> productLines) {
		this.productLines = productLines;
	}

	public void addProductLine(ProductLine line) {
		if (!productLines.contains(line)) {
			productLines.add(line);
			line.setProduct(this);
		}
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Product{" + "id=" + id + ", name=" + name + ", price=" + price + ", consumerType=" + consumerType
				+ ", createAt=" + createAt + ", status=" + status + ", productLines=" + productLines + ", category="
				+ category + '}';
	}
}
