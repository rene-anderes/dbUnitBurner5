package org.anderes.edu.dbunitburner.sample.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@NamedQuery(
    name = "Recipe.findAllTag",
    query = "select r.tags from Recipe r"
)
public class Recipe implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(nullable = false)
	private String uuid;

	@Version
	private Integer version;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date lastUpdate = new Date();
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
    private Date addingDate = new Date();

	@NotNull
    @Size(min = 1, max = 80)
	@Column(nullable = false, length = 80)
	private String title;

	@Valid
	@Embedded
	@Column(nullable = true)
	private Image image;

	@NotNull @Valid @Size(min = 1, max = 100)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "RECIPE_ID")
	private List<Ingredient> ingredients = new ArrayList<Ingredient>();

	@NotNull
    @Size(min = 8, max = 8000)
	@Column(nullable = false, length = 8000)
	private String preparation;

    @Size(min = 0, max = 8000)
	@Column(nullable = true, length = 8000)
	private String preamble;

	@NotNull @Size(min = 1, max = 10)
    @Column(nullable = false, length = 10)
	private String noOfPerson;
	
	@NotNull @Size(min = 0, max = 100)
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="TAGS", joinColumns=@JoinColumn(name="RECIPE_ID"))
	private Set<String> tags = new HashSet<String>();
	
	@NotNull @Min(0) @Max(5)
	@Column(nullable = false)
	private Integer rating = Integer.valueOf(0);

	/*package*/ public Recipe() {
	    super();
    }
	
	public Recipe(final String uuid) {
        this();
        this.uuid = uuid;
    }

    public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Integer getVersion() {
		return version;
	}

	public String getUuid() {
        return uuid;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by the Date object.
     * @return time
     */
    public Long getLastUpdateTime() {
        return this.lastUpdate.getTime();
    }

    public Date getAddingDate() {
        return addingDate;
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by the Date object.
     * @return time
     */
    public Long getAddingDateTime() {
        return this.addingDate.getTime();
    }
    
    public void setAddingDate(Date addingDate) {
        this.addingDate = addingDate;
    }

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void addIngredient(final Ingredient ingredient) {
		ingredients.add(ingredient);
	}
	
	public void removeIngredient(final Ingredient ingredient) {
		ingredients.remove(ingredient);
	}

	public List<Ingredient> getIngredients() {
		return Collections.unmodifiableList(ingredients);
	}

	public String getPreparation() {
		return preparation;
	}

	public void setPreparation(final String preparation) {
		this.preparation = preparation;
	}

	public String getPreamble() {
		return preamble;
	}

	public void setPreamble(final String preamble) {
		this.preamble = preamble;
	}

	public String getNoOfPerson() {
		return noOfPerson;
	}

	public void setNoOfPerson(final String noOfPerson) {
		this.noOfPerson = noOfPerson;
	}

	public Set<String> getTags() {
		return Collections.unmodifiableSet(tags);
	}

	public Recipe addTag(final String tag) {
		this.tags.add(tag);
		return this;
	}
	
	public void removeTag(final String tag) {
		this.tags.remove(tag);
	}

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("uuid", uuid).append("title", title).append("preamble", preamble)
            .append("image", image).append("noOfPerson", noOfPerson).append("ingredients", ingredients)
            .append("preparation", preparation).append("tags", tags).append("lastUpdate", lastUpdate)
            .append("addingUpdate", addingDate).append("rating", rating).toString();
    }
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(uuid).append(title).append(image)
				.append(preamble).append(preparation).append(noOfPerson)
				.append(lastUpdate).append(addingDate).append(rating)
				.append(ingredients).append(tags).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Recipe rhs = (Recipe) obj;
		return new EqualsBuilder().append(uuid, rhs.uuid).append(title, rhs.title)
				.append(preamble, rhs.preamble).append(lastUpdate, rhs.lastUpdate)
				.append(addingDate, rhs.addingDate).append(rating, rhs.rating)
				.append(noOfPerson, rhs.noOfPerson).append(image, rhs.image)
				.append(ingredients, rhs.ingredients)
				.append(tags, rhs.tags).append(preparation, rhs.preparation).isEquals();
	}
}
