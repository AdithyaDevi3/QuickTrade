//package entity;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//
//@Entity
//@Table
//public class Stocks {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private String id;
//
//    @Column(name = "Stock_Name")
//    private String fullName;
//    @Column(name = "Stock_Abbreviation")
//    private String abrv;
//    @Column(name ="Stock_Price")
//    private int price;
//    @Column(name ="Stock_Growth")
//    private double growth;
//    @Column(name = "Stock_Projection")
//    private double projection;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public String getAbrv() {
//        return abrv;
//    }
//
//    public void setAbrv(String abrv) {
//        this.abrv = abrv;
//    }
//
//    public int getPrice() {
//        return price;
//    }
//
//    public void setPrice(int price) {
//        this.price = price;
//    }
//
//    public double getGrowth() {
//        return growth;
//    }
//
//    public void setGrowth(double growth) {
//        this.growth = growth;
//    }
//
//    public double getProjection() {
//        return projection;
//    }
//
//    public void setProjection(double projection) {
//        this.projection = projection;
//    }
//
//    @Override
//    public String toString() {
//        return "Stocks{" +
//                "id='" + id + '\'' +
//                ", fullName='" + fullName + '\'' +
//                ", abrv='" + abrv + '\'' +
//                ", price=" + price +
//                ", growth=" + growth +
//                ", projection=" + projection +
//                '}';
//    }
//}
//
//
