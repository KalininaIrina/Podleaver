package com.podlive.tracker.account.mapper;

import com.podlive.tracker.account.dto.AccountResponseDto;
import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.common.service.GenericMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper extends GenericMapper<Account, AccountResponseDto> {
}
