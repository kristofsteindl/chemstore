import axios from "axios";
import { GET_ERRORS } from "./types";

export const createChemItem = (newChemItem, labKey, history) => async dispatch => {
    try {
        await axios.post(`/api/chem-item/${labKey}`, newChemItem)
        history.push('/chem-items')
    } catch (err) {
        dispatch({
            type: GET_ERRORS,
            payload: err.response.data
        })
    }
}