package entity;

import jdk.jfr.events.CertificateId;

@Collection(name ="")
public class Stocks {
    @Id
    String id;

    @Field(name ="FullName")
    String fullName;

    @Field(name = "Abrv")
    String abrv;

     @Field(name = "Price")
    int price;

     @Field(name = "growth")
    double growth;

     @Field(name = "Projection")
    double projection;

}
