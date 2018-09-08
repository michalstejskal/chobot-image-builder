package cz.chobot.image_builder.bo

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "module")
data class
Module(
        @NotNull
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "module_id", unique = true, nullable = false)
        val id: Long,

        @NotNull
        @Column(name = "type", nullable = false)
        var type: Int,

        @NotNull
        @Size(max = 64)
        @Column(name = "response_class", nullable = false)
        var responseClass: String,

        @NotNull
        @Column(name = "code", nullable = false)
        var code: String,

        @NotNull
        @Size(max = 64)
        @Column(name = "name", nullable = false)
        var name: String,

        @OneToMany(fetch = FetchType.EAGER, mappedBy = "module", cascade = [CascadeType.ALL])
        var versions: MutableList<ModuleVersion>,

        @Size(max = 64)
        @OneToOne
        @JoinColumn(name = "module_version_id")
        var actualVersion: ModuleVersion,

        @NotNull
        @Column(name = "status", nullable = false)
        var status: Int,

        @NotNull
        @Column(name = "repo_url")
        var repositoryUrl: String,

        @NotNull
        @Column(name = "connection_uri", nullable = false)
        var connectionUri: String,

        @NotNull
        @Column(name = "docker_registry", nullable = false)
        var dockerRegistry: String,

        @Column(name = "docker_id")
        var dockerId: String,


        @JsonIgnore
        @ManyToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "network_id")
        var network: Network
)