package com.antonio.authserver.service;
import java.util.List;
import java.util.Optional;

import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.dto.ClientDto;
import com.antonio.authserver.entity.Client;
import com.antonio.authserver.mapper.ClientMapper;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.ClientRepository;

@Service
public class ClientService {

    private ClientRepository clientRepository;
    private JwtService jwtService;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public ClientService(ClientRepository clientRepository, JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }


	private ClientMapper clientMapper;

	@Autowired
	public ClientService(ClientMapper clientMapper) {
		this.clientMapper = clientMapper;
	}
	public List<ClientDto> getAllClients() {
		System.out.println(clientRepository.findAll());
		return clientMapper.toClientDtoList(clientRepository.findAll());
	}

	public ClientDto getClientBySecretAndNameWithRealm(String realmName, String clientName, String clientSecret) {
		Client client = clientRepository.findByClientNameAndClientSecretAndRealmName(clientName, clientSecret, realmName);
		return clientMapper.toClientDto(client);
	}

	public ClientDto getClientByName(String clientName) throws CustomException {
		Client client = clientRepository.findByClientName(clientName).orElseThrow(() -> new CustomException(
				"Client with the name [ " + clientName + " ] could not be found!", HttpStatus.NOT_FOUND));
		return clientMapper.toClientDto(client);

	}

	public void saveClient(ClientDto client) throws CustomException {
		client.setClientName(client.getClientName().replaceAll("\\s+", ""));
		Optional<Client> byClientName = clientRepository.findByClientName(client.getClientName());
		if (byClientName.isPresent())
			throw new CustomException(
					"Client with the name [ " + byClientName.get().getClientName() + " ] already exists!",
					HttpStatus.CONFLICT);
		else if (client.getClientName().equals("")) {
			throw new CustomException("The inserted client cannot be null!", HttpStatus.BAD_REQUEST);
		} else {

			clientRepository.save(clientMapper.toClientDao(client));
		}
	}

	public void updateClientByName(String name, ClientDto clientDto) throws CustomException {
		clientDto.setClientName(clientDto.getClientName().replaceAll("\\s+", ""));
		Client client = clientRepository.findByClientName(name)
				.orElseThrow(() -> new CustomException("Client with the name [ " + name + " ] could not be found!",
						HttpStatus.NOT_FOUND));
		if (clientDto.getClientName().equals(""))
			throw new CustomException("The inserted client cannot be null!", HttpStatus.BAD_REQUEST);
		client.setClientName(clientDto.getClientName());
		clientRepository.save(client);
	}

	public void deleteClientByName(String clientName) throws CustomException {
		Client client = clientRepository.findByClientName(clientName).orElseThrow(() -> new CustomException(
				"Client with the name [ " + clientName + " ] could not be found!", HttpStatus.NOT_FOUND));
		clientRepository.delete(client);
	}

    public Code generateCode(AppUserDto user) {

        final Code code = createOauthCode(user);

        return code;
    }

    public void validateClient(String clientId, String clientSecret ) {

        final ClientDto client = getClientByName(clientId);
        verifyClientCredential(clientSecret, client.getClientSecret());

    }

    private void verifyClientCredential(String currentPassword, String storedPassword) {

        if (!passwordEncoder.matches(currentPassword, storedPassword)) {
            throw new CustomException("Client has not permission to use the authorization server", HttpStatus.UNAUTHORIZED);
        }
    }

    private Code createOauthCode(AppUserDto user) {
        String jwtCode = generateTokenCode(user);

        if (!jwtCode.equals("")) {
            final Code code = new Code(jwtCode);
            return code;
        }
        return null;
    }

    private String generateTokenCode(AppUserDto userDto) {

        long expirationTime = System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME;

        String token = jwtService.createAccessToken(userDto.getUsername(), expirationTime, new ArrayList<>(),
                SecurityConstants.TOKEN_SECRET);

        return token;
    }


}
