package com.cie.hr.common.constant;

/**
 * @author Alexis TAMBIE
 * @created 06/05/2023
 * @project hr-cie
 */
public class Constant {
    public static final String EMAIL_ALREADY_EXISTS = "l'email existe déjà";
    public static final String NO_USER_FOUND_BY_USERNAME = "Aucun utilisateur trouvé par nom d'utilisateur :";
    public static final String FOUND_USER_BY_USERNAME = "Renvoyer l'utilisateur trouvé par nom d'utilisateur : ";
    public static final String NO_USER_FOUND_BY_EMAIL = "Aucun utilisateur trouvé pour l'e-mail : ";

    public static final String HTTP_MESSAGE_OK = "Opération réalisée avec succès";
    public static final long EXPIRATION_TIME_RESET_PASSWORD = 600_000;
    public static final long EXPIRATION_TIME = 432_000_000; // 5 days expressed in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Le token ne peut pas être vérifié";
    public static final String GET_ARRAYS_LLC = "Compagnie Ivoirienne D'Electricité";
    public static final String GET_ARRAYS_ADMINISTRATION = "User Management Portal";
    public static final String AUTHORITIES = "profile";
    public static final String FORBIDDEN_MESSAGE = "Vous devez vous connecter pour accéder à cette ressource";
    public static final String ACCESS_DENIED_MESSAGE = "Vous n'avez pas la permission d'accéder à cette ressource";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
}
