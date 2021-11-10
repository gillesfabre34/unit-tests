package com.airbus.retex.service.impl.request.mapper;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.business.mapper.ReferenceMapper;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.admin.UserRepository;
import com.airbus.retex.persistence.client.ClientRepository;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.airbus.retex.business.mapper.MapperUtils.makeIsSameLambda;
import static com.airbus.retex.business.mapper.MapperUtils.updateList;


@Mapper(componentModel = "spring", uses = ReferenceMapper.class)
public abstract class HistorizationRequestMapper extends AbstractMapper {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserRepository userRepository;


    @Mapping(target = "operators", ignore = true)
    @Mapping(target = "technicalResponsibles", ignore = true)
    // We don't delete any child request nor restore any deleted child request)
    @Mapping(target = "childRequests", ignore = true)
    @Mapping(target = "clients", ignore = true)
    @Mapping(target = "translates", ignore = true)
    public abstract void copyFrom(Request source, @MappingTarget Request target);

    /**
    @AfterMapping
    public void mapTranslateLists(Request source, @MappingTarget Request target) {
        BiFunction<Translate, Translate, Boolean> isSame = (s,d) -> s.getLanguage().equals(d.getLanguage()) && s.getField().equals(d.getField());
        Supplier<Translate> newDestinationInstance = Translate::new;
        BiConsumer<Translate, Translate> mapSourceToDestination = this::mapTranslate;

        updateList(source.getTranslates(), target.getTranslates(), isSame, newDestinationInstance, mapSourceToDestination);
        //FIXME Unable to get Translate list of entity that come from audit, it seems there is a conflict with the @Where on the relationship
    }

    public abstract void mapTranslate(Translate source, @MappingTarget Translate target);
    **/

    @AfterMapping
    public void mapClientLists(Request source, @MappingTarget Request target) {
        BiFunction<Client, Client, Boolean> isSame = makeIsSameLambda(Client::getId);
		Function<Client, Client> resolveDestinationFromSource = s -> clientRepository.getOne(s.getId());

        updateList(source.getClients(), target.getClients(), isSame, resolveDestinationFromSource);
    }


    @AfterMapping
    public void mapUserLists(Request source, @MappingTarget Request target) {
        BiFunction<User, User, Boolean> isSame = makeIsSameLambda(User::getId);
		Function<User, User> resolveDestinationFromSource = s -> userRepository.getOne(s.getId());

        updateList(source.getOperators(), target.getOperators(), isSame, resolveDestinationFromSource);
        updateList(source.getTechnicalResponsibles(), target.getTechnicalResponsibles(), isSame, resolveDestinationFromSource);
    }




}

