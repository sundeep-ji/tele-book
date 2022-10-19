package com.telebook.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "This field can't be empty!!")
    @Size(min = 3, max = 25, message = "Length should be between 3-25 character!!")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "This field can't be empty!!")
    @Email(message = "Please Enter a valid Email Address")
    private String email;

    @NotBlank(message = "This field can't be empty")
    private String password;

    @Transient
    private String rePassword;

    private String role = "ROLE_USER";

    private String profile = "avatar.png";

    private boolean status = true;

    @AssertTrue(message = "You have not accepted terms and conditions!")
    private boolean terms;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Contact> contacts;

}
