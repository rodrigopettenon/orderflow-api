package com.rodrigopettenon.orderflow.utils;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.leftPad;

public class StringsValidation {

    //Validações para client

    private static final Pattern PADRAO_EMAIL_VALIDO = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public static boolean isValidEmail(String email){
        if(isNotBlank(email)){
            return PADRAO_EMAIL_VALIDO.matcher(email.trim()).matches();
        }
        return false;
    }

    public static String denormalizeCpf(String cpf){
        if(isNotBlank(cpf)){
            return cpfWithFullDigits(cpf);
        }
        return null;
    }

    public static String cpfWithFullDigits(String cpf){
        return leftPad(removeNonNumericCharacters(cpf), 11, '0');
    }

    public static String removeNonNumericCharacters(String texto){
        if(isNotBlank(texto)){
            return texto.replaceAll("[^\\d]", "");
        }
        return null;
    }

    public static boolean isValidCPF(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }

        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;

        return firstDigit == (cpf.charAt(9) - '0') && secondDigit == (cpf.charAt(10) - '0');
    }

    public static String removeAllSpaces(String input) {
        if (input == null) {
            return null;  // Campo nulo, retorna nulo.
        }
        String result = input.replaceAll("\\s+", "");
        return result;
    }

    public static String normalizeSpaces(String input) {
        if (input == null) {
            return null;  // Se o campo for nulo, apenas retorna nulo.
        }
        // Remove espaços extras no início e no fim, e substitui múltiplos espaços por apenas um espaço.
        return input.trim().replaceAll("\\s+", " ");
    }

    //Validações para products

    public static boolean isAlphanumeric(String sku) {
        return sku != null && sku.matches("^[a-zA-Z0-9]+$");
    }

}
