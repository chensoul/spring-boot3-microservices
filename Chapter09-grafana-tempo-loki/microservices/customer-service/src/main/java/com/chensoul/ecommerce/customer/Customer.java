package com.chensoul.ecommerce.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class Customer {

  @Id
  private String id;

  @Indexed(unique = true)
  private Integer customerId;

  @Version
  private Integer version;

  private String firstname;

  private String lastname;

  private String email;

  private String street;

  private String zipCode;
}
