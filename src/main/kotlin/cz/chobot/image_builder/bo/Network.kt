package cz.chobot.image_builder.bo

import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "network")
data class Network(
        @NotNull
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "network_id", unique = true, nullable = false)
        val id: Long,

        @NotNull
        @OneToOne
        @JoinColumn(name = "network_type_id")
        val type: NetworkType,

        @NotNull
        @Size(max = 64)
        @Column(name = "name", nullable = false)
        val name: String,

        @NotNull
        @Size(max = 64)
        @Column(name = "tag", nullable = false)
        val tag: String,

        @NotNull
        @Column(name = "status", nullable = false)
        val status: Int,

        @NotNull
        @Column(name = "connection_uri", nullable = false)
        val connectionUri: String,

        @NotNull
        @Column(name = "docker_registry", nullable = false)
        val dockerRegistry: String
)