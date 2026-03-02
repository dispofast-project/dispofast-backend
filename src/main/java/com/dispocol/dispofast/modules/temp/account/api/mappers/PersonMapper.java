package com.dispocol.dispofast.modules.temp.account.api.mappers;

import com.dispocol.dispofast.modules.temp.account.Person;
import com.dispocol.dispofast.modules.temp.account.api.dtos.PersonResponseDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {OrganizationMapper.class})
public interface PersonMapper {

  PersonResponseDTO toResponseDTO(Person person);

  List<PersonResponseDTO> toResponseDTOList(List<Person> persons);
}
