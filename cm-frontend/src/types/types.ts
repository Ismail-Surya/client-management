export interface User {

    id: number;
    username: string;
    email: string;
    groups: string[]

}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    accessExpiresInMs: number;
    refreshExpiresInMs: number;
    user: User;
}

export interface LoginRequest {
    usernameOrEmail: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export interface Client {
    id: number;
    firstName: string;
    middleName: string | null;
    lastName: string;
    email: string;
    mobileNumber: string;
    panNumber: string;
}

export interface ClientRequest {
    firstName: string;
    middleName: string | null;
    lastName: string;
    email: string;
    mobileNumber: string;
    panNumber: string;
}

export interface FieldErrors {
    [field: string]: string;
}

export interface ApiError {
    timestamp: string;
    status: string;
    error: string;
    message: string;
    path: string;
    fieldErrors?: FieldErrors
}









