package net.teleuptv.braintree.customer.model;

import java.util.ArrayList;
import java.util.List;

import com.braintreegateway.Subscription;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.teleuptv.common.BaseEntity;

@Entity
@Table(name="domain_app_customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DomainAppCustomer extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="da_customer_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;

    @Column(name="phone")
    private String phone;

    @Column(name="bt_customer_id")
    private String btCustomerId;

    @Column(name="bt_plan_id")
    @ElementCollection
    @CollectionTable(name = "customer_bt_plans", joinColumns = @JoinColumn(name = "da_customer_id"))
    private List<String> btPlanId = new ArrayList<>();

    @Column(name="bt_subscription_id")
    @ElementCollection
    @CollectionTable(name = "customer_bt_subscriptions", joinColumns = @JoinColumn(name = "da_customer_id"))
    private List<String> btSubscriptionId = new ArrayList<>();

    @Column(name="subscription_status")
    private Subscription.Status status;

}
