package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.clients.banks.privatbank.PrivatbankClient;
import com.flybird.nestwise.domain.Account;
import com.flybird.nestwise.domain.Bank;
import com.flybird.nestwise.domain.ExchangeRate;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.BankTransactionDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import com.flybird.nestwise.repositories.BankRepository;
import com.flybird.nestwise.repositories.CurrencyRepository;
import com.flybird.nestwise.utils.MappingUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.flybird.nestwise.utils.MappingUtil.CURRENCY_MAPPING;
import static java.util.Objects.nonNull;

@Service("privatbank")
@RequiredArgsConstructor
public class PrivatbankServiceImpl implements BankService {
    private final PrivatbankClient privatbankClient;
    private final BankRepository bankRepository;
    private final CurrencyRepository currencyRepository;
    private final MappingUtil mappingUtil;
    private Bank bank;

    @PostConstruct
    private void init() {
        bank = bankRepository.findByCode("privatbank").orElseThrow(RuntimeException::new);
    }

    @Override
    public LoginStatusResponseDto bankLogin(String bankId, AuthType type, LoginRequestDto requestDto) {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, List<BankTransactionDto>> getTransactions(long from, long to) {
        return Map.of();
    }

    @Override
    public List<Account> getAccounts(Long userId) {
        return List.of();
    }

    @Override
    public Map<Pair<Integer, Integer>, ExchangeRateDto> getCurrentExchangeRates() {
        return Map.of();
    }

    @Override
    public List<ExchangeRate> getHistoricalExchangeRates(LocalDate date) {
        return privatbankClient.getHistoricalExchangeRates(date).getExchangeRate().stream()
                .filter(rate -> nonNull(rate.getSaleRate()) && CURRENCY_MAPPING.containsKey(rate.getCurrency()))
                .map(exchangeRate -> mappingUtil.toDomain(exchangeRate, bank.getId(), bankRepository::getReferenceById,
                        currencyRepository::getReferenceById, date))
                .toList();
    }
}