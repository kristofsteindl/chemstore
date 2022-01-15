import { FILL_LAB_DROPDOWN } from "../actions/types";

const initialState = []

export default function selectedLabReducer(state = initialState, action) {
    switch (action.type) {
        case FILL_LAB_DROPDOWN:
            console.log("state in selectedLabReducer: " + state)
            return action.payload.map(lab => {return {...lab, id: lab.id, value: lab.key, label: lab.name}})
            
            
        default: 
            return state
    }
}