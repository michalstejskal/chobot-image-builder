package cz.chobot.image_builder.bo

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "network_type")
data class NetworkType(
        @NotNull
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "network_type_id", unique = true, nullable = false)
        val id: Long,

        @NotNull
        @Column(name = "name", nullable = false)
        val name: String,

        @JsonIgnore
        @NotNull
        @Column(name = "image_id", nullable = false)
        val imageId: String
){
        override fun hashCode(): Int {
                return id.hashCode()
        }

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || javaClass != other.javaClass) return false
                val that = other as NetworkType?
                return id == that?.id && name == that?.name
        }

        override fun toString(): String {
                return "$id - $name"
        }
}