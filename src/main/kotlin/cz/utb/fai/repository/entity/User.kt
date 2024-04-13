package cz.utb.fai.repository.entity

import cz.utb.fai.repository.type.Role
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "USER")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,
    @Enumerated(EnumType.STRING)
    var role: Role
) {
    constructor() : this(firstName = "", lastName = "", email = "", password = "", role = Role.USER)
}