package my.assignment.contentprovidertwo;

/**
 * Created by root on 9/12/16.
 */

public class Contact {
    String phone;
    String name;

    public Contact(String name, String phone){
        this.name=name;
        this.phone=phone;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



}
