package com.inesv.mapper;

import com.inesv.model.Address;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressMapper {

    Address getAddressByCondition(Address address);

    List<Address> getAddressByConditions();

    Address queryAddressInfo(Long coinNo);



}
