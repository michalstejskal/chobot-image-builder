package cz.chobot.image_builder.bo

import javax.persistence.Entity
import javax.persistence.Table
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "module_version")
data class ModuleVersion(
        @NotNull
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "module_version_id", unique = true, nullable = false)
        val id: Long,

        @NotNull
        @Size(max = 64)
        @Column(name = "name", nullable = false)
        var name: String,

        @Size(max = 256)
        @Column(name = "commit_id")
        var commitId: String,

        @JsonIgnore
        @ManyToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "module_id")
        var module: Module

)