import axios from "axios";
import { GET_ERRORS } from "./types";

export const createNewUser = (newUser, history) => async dispatch => {
    try {
        await axios.post("/api/account/user", newUser)
        history.push("/users")
    } catch (error) {
        dispatch({
            type:GET_ERRORS,
            payload: error.response.data
        });
    }
}

