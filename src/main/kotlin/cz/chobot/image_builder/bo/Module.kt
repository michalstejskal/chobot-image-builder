package cz.chobot.image_builder.bo

import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

//@Entity
//@Table(name = "module")
data class Module(
//        @NotNull
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        @Column(name = "module_id", unique = true, nullable = false)
        val id: Long,

//        @NotNull
//        @Size(max = 64)
//        @Column(name = "name", nullable = false)
        val name: String,

//        @NotNull
//        @Column(name = "type", nullable = false)
        val type: Int,

//        @NotNull
//        @Size(max = 64)
//        @Column(name = "image_name", nullable = false)
        val imageName: String,

//        @NotNull
//        @Size(max = 64)
//        @Column(name = "image_id", nullable = false)
        val imageId: String,

//        @NotNull
//        @Size(max = 64)
//        @Column(name = "tag", nullable = false)
        val tag: String,

//        @NotNull
//        @Column(name = "status", nullable = false)
        val status: Int,

//        @NotNull
//        @Column(name = "connection_uri", nullable = false)
        val connectionUri: String,


//        @NotNull
//        @Column(name = "username", nullable = false)
        val username: String,

//        @NotNull
//        @Column(name = "docker_registry", nullable = false)
        val dockerRegistry: String,


//        @OneToMany(fetch = FetchType.EAGER, mappedBy = "module", cascade = [CascadeType.ALL])
        val userCodes: List<UserCode>
)