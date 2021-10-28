import React from 'react'
import { Link } from 'react-router-dom'

const CreateChemItemButton = () => {
    return (
        <React.Fragment>
            <Link to="/addChemItem" className="btn btn-lg btn-info">
                Create Chem Item
            </Link>
        </React.Fragment>
    )
}

export default CreateChemItemButton
