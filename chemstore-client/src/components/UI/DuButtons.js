import { Button } from "@mui/material"
import { Link } from "react-router-dom"
import "./DuButtons.css"

const DuButtons = props => {
    return (
        <div id="button-group">
            <Link to={props.updateFormTo} >
                <Button className="btn" variant="outlined" size="small">
                    <i className="fa fa-edit">Update</i>
                </Button>
            </Link>
            
            <Button className="btn" color="error" variant="outlined" size="small" onClick={props.onDelete}  >
                <i className="fa fa-minus-circle">Delete</i>
            </Button>
        </div>
    )
}

export default DuButtons