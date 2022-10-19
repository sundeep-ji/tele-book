package com.telebook.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "CONTACTS")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "This field can't be empty!!")
    @Size(min = 3, max = 25, message = "Length should be between 3-25 character!!")
    private String name;

    @Size(min = 10, max = 10, message = "Enter 10 digit mobile number!!")
    @NotBlank(message = "This field can't be empty!!")
    @Pattern(regexp = "[1-9]{1}[0-9]{9}", message = "Please Enter valid number")
    private String phone;

    @Email(message = "Please Enter a valid email")
    private String email;

    private String work;

    private String avatar;

    @ManyToOne
    private User user;

}
