import axios from "axios"
import { useEffect, useState } from "react"
import Select from "react-dropdown-select"
import { useSelector } from "react-redux"
import { check } from "../../utils/securityUtils"
import MixtureCard from "./MixtureCard"
import MixtureHeader from "./MixtureHeader"

const MixtureDashboard = () => {

    const selectedLab = useSelector(state => state.selectedLab)
    const user = useSelector((state) => state.security.user)

    const [ mixtures, setMixtures ] = useState([])
    const [ projects, setProjects ] = useState([])

    const [ page, setPage ] = useState(0)
    const [ size, setSize ] = useState(20)
    const [ selectedProject, setSelectedProject ] = useState("")
    
    const queryString = () => {
        let queryString = `?page=${page}&size=${size}`
        queryString = `${queryString}${selectedProject ? ('&projectId=' + selectedProject.id) : ''}`
        return queryString
    }

    const isManager =  (selectedLab.key) && selectedLab.labManagers.filter(manager => manager.username === user.username).length > 0

    useEffect(() => {
        if (selectedLab) {
            axios.get(`/api/logged-in/project/${selectedLab.key}`).then(result => setProjects(result.data))
        }
        
    }, [selectedLab])

    useEffect(() => {
        check()
        if (selectedLab) {
            axios.get(`/api/mixture/${selectedLab.key}${queryString()}`)
                .then(result => setMixtures(result.data.content))
        }
    }, [selectedProject, page, size])
   

    useEffect(() => {
        check()
        axios.get(`/api/mixture/${selectedLab.key}${queryString()}`)
            .then(result => setMixtures(result.data.content))
    }, [])

    const deleteMixture = async mixtureId => {
        await axios.delete(`/api/mixture/${mixtureId}`)
        setMixtures(originalList => originalList.filter(mixture => mixture.id !== mixtureId))
    }

    return (
        
        <div className="container col-md-12">
            <h3 className="display-4 text-center">Mixtures</h3>
                <p className="lead text-center">List the mixtures and eluents of {selectedLab.name}</p>
                <br/>
                <div className="form-group row mb-3">
                <label htmlFor="chemical" className="col-sm-2 col-form-label">project</label>
                <div className="col-sm-10">
                    <Select
                        options={projects}
                        labelField="name"
                        valueField="name"
                        placeholder="project"
                        searchable={false}
                        clearable={false}
                        style={{height: "42px", fontSize: "16px"}}
                        onChange={items => setSelectedProject(items[0] ? items[0] : "")}
                    />
                </div>
            </div>
            <MixtureHeader />
            <hr />
            {mixtures.map(mixture => 
                <MixtureCard 
                    key={mixture.id} 
                    mixture={mixture}
                    deleteMixture={deleteMixture}
                    isManager={isManager}
                />
            )}
        </div>
    )
}

export default MixtureDashboard