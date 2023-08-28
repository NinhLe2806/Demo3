package vn.vnpay.demo3.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.N;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {
    private String userId;
    private String username;
    private String password;
}
