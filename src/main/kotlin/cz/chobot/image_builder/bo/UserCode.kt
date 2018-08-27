package cz.chobot.image_builder.bo

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotNull

//@Entity
//@Table(name = "user_code")
data class UserCode(
//        @NotNull
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        @Column(name = "user_code_id", unique = true, nullable = false)
        val id: Long,

//        @ManyToOne(cascade = [CascadeType.ALL])
//        @JoinColumn(name = "module_id")
//        val module: Module,

//        @NotNull
//        @Column(name = "encoded_code", nullable = false)
        val encodedCode: String,

//        @NotNull
//        @Column(name = "response_class", nullable = false)
        val responseClass: String
)