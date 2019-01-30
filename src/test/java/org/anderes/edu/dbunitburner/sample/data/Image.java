
package org.anderes.edu.dbunitburner.sample.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Embeddable
public class Image implements Serializable {

	private static final long serialVersionUID = 1L;
    @Size(min = 2, max = 255)
	@Column(name = "IMAGE_URL")
    private String url;
	@Size(min = 0, max = 50)
    @Column(name = "IMAGE_DESCRIPTION", length = 50)
    private String description;
	@Temporal(TemporalType.DATE)
    @Column(name = "IMAGE_DATE")
    private Date imageDate = new Date();

    /*package*/ Image() {
    }

    public Image(String url, String description) {
        super();
        this.url = url;
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getImageDate() {
        return new Date(imageDate.getTime());
    }

    public void setImageDate(Date imageDate) {
        this.imageDate = new Date(imageDate.getTime());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("url", url).append("description", description).build();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(url).append(description).toHashCode();

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
        Image rhs = (Image) obj;
        return new EqualsBuilder().append(url, rhs.url).append(description, rhs.description).isEquals();
    }

}
