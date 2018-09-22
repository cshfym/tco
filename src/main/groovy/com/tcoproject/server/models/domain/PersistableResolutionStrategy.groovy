package com.tcoproject.server.models.domain

import org.hibernate.annotations.GenericGenerator

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * Entity to store resolution strategies for non-standard model and trim names
 */
@Entity(name="resolution_strategy")
class PersistableResolutionStrategy {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    // Source of price data - i.e. KBB
    String source

    // Source make
    String makeName

    // Source model
    String modelName

    // The target type returned for value returned - Model or Trim
    String targetType

    // Source field to look for the key - generally the Trim
    String keySource

    // Key to look for in the keySource
    String key

    // Value to return if the key is found
    String value

    boolean active
}
