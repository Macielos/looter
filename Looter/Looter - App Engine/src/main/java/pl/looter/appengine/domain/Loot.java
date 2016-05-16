package pl.looter.appengine.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import lombok.Data;

/**
 * Created by Arjan on 14.05.2016.
 */
@Entity
@Data
public class Loot {

	@Id
	private Long id;

	private String name;

	private String description;

	private String image;
}
