import React, {useEffect, useState} from 'react'
import makeStyles from "@material-ui/core/styles/makeStyles";
import useTheme from "@material-ui/core/styles/useTheme";
import CssBaseline from "@material-ui/core/CssBaseline";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import Hidden from "@material-ui/core/Hidden";
import Drawer from "@material-ui/core/Drawer";
import MenuIcon from '@material-ui/icons/Menu';
import {Avatar, Divider} from "@material-ui/core";
import ListItem from "@material-ui/core/ListItem";
import List from "@material-ui/core/List";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import ReceiptIcon from '@material-ui/icons/Receipt';
import InsertInvitationIcon from '@material-ui/icons/InsertInvitation';
import ExploreIcon from '@material-ui/icons/Explore';
import PersonIcon from '@material-ui/icons/Person';
import SettingsApplicationsIcon from '@material-ui/icons/SettingsApplications';
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import './skeleton.css'
import { useDispatch } from 'react-redux';
import { getMyProfile, signOut } from '../store/action';
import store from '../store';
import { NavLink, Redirect, Route, Switch } from 'react-router-dom';
import Profile from './opt/Profile';
import Posts from './opt/Posts';
import PostPage from './opt/PostsPage';
import OtherProfile from './opt/OtherProfile';
import PostDetails from './opt/PostDetails';

const drawerWidth = 240;
const useStyles = makeStyles((theme) => ({
    root: {
        display: "flex"
    },
    rootFull: {
        width: "100%"
    },
    paper: {
        padding: theme.spacing(2),
        textAlign: 'center',
        color: theme.palette.text.secondary,
    },
    media: {
        height: 0,
        paddingTop: '56.25%', // 16:9
    },
    canvasPaper: {
        padding: theme.spacing(1),
        color: theme.palette.text.secondary,
    },
    drawer: {
        [theme.breakpoints.up('sm')]: {
            width: drawerWidth,
            flexShrink: 0,
        },
    },
    full:{
        width: drawerWidth-50
    },
    appBar: {
        [theme.breakpoints.up('sm')]: {
            width: `calc(100% - ${drawerWidth}px)`,
            marginLeft: drawerWidth,
        },
    },
    menuButton: {
        marginRight: theme.spacing(2),
        [theme.breakpoints.up('sm')]: {
            display: 'none',
        },
    },
    toolbar: theme.mixins.toolbar,
    drawerPaper: {
        width: drawerWidth,
    },
    content: {
        flexGrow: 1,
        padding: theme.spacing(0,0),
    },
}));

const Skeleton=props=>{

    const { window } = props;
    const classes = useStyles();
    const theme = useTheme();
    const [mobileOpen, setMobileOpen] = useState(false);
    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen);
    };
    
    const dispatch = useDispatch()
    const [avatar, setAvatar] = useState(null)

    useEffect( () => {
      dispatch(getMyProfile())  
      return store.subscribe( () => {
        const newAvatar = store.getState().profile.imageURL
        if (avatar !== newAvatar)
          setAvatar(newAvatar)
      })
    }, [])

    const container = window !== undefined ? () => window().document.body : undefined;
    const drawer=(
        <div>
            <List style={{marginTop:'55px'}}>
                <Divider/>
                <ListItem style={{padding:'20px'}} component={NavLink} to="/user/posts" activeClassName="Mui-selected">
                    <ListItemIcon><ReceiptIcon /> </ListItemIcon>
                    <ListItemText primary={'Posts'} />
                </ListItem>
                <Divider/>
                <ListItem style={{padding:'20px'}} component={NavLink} to="/user/events" activeClassName="Mui-selected" exact>
                    <ListItemIcon><InsertInvitationIcon /> </ListItemIcon>
                    <ListItemText primary={'Events'} />
                </ListItem>
                <Divider/>
                <ListItem style={{padding:'20px'}} component={NavLink} to="/user/explore" activeClassName="Mui-selected" exact>
                    <ListItemIcon><ExploreIcon /> </ListItemIcon>
                    <ListItemText primary={'Explore'} />
                </ListItem>
                <Divider/>
                <ListItem style={{padding:'20px'}} component={NavLink} to="/user/profile" activeClassName="Mui-selected" exact>
                    <ListItemIcon><PersonIcon/> </ListItemIcon>
                    <ListItemText primary={'Profile'} />
                </ListItem>
                <Divider/>
                <ListItem style={{padding:'20px'}} component={NavLink} to="/user/settings" activeClassName="Mui-selected" exact>
                    <ListItemIcon><SettingsApplicationsIcon /> </ListItemIcon>
                    <ListItemText primary={'Settings'} />
                </ListItem>
                <Divider/>
                <ListItem style={{padding:'20px'}} onClick={() => dispatch(signOut())} button>
                    <ListItemIcon><ExitToAppIcon /> </ListItemIcon>
                    <ListItemText primary={'Sign out'} />
                </ListItem>
                <Divider/>
            </List>
        </div>
    )

    const mainContentSelection = (
    <Switch>
      <Route path="/user/profile" exact>
        <Profile />
      </Route>
      <Route path="/user/profile/:username" exact>
        <OtherProfile />
      </Route>
      <Route path="/user/posts" exact>
        <PostPage />
      </Route>
      <Route path="/user/post/:id">
          <PostDetails />
      </Route>
      <Route path="/user/events" exact>
        <>
        Events
        </>
      </Route>
      <Route path="/user/settings" exact>
        <>
        Settings
        </>
      </Route>
      <Route path="/user/explore" exact>
        <Posts url={'/user/posts?for=all'} />
      </Route>
      <Redirect to="/user/profile" />
    </Switch>
    )

    return(
        <div className={classes.root}>
            <CssBaseline />
            <AppBar style={{backgroundColor:'#0090ff'}} position="fixed" className={classes.appBar}>
                <Toolbar>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        edge="start"
                        onClick={handleDrawerToggle}
                        className={classes.menuButton}
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography variant="h6" noWrap>
                        BloodBook
                    </Typography>
                    <div  style={{position:'absolute',right:'20px',display:'flex'}}>
                        <Avatar src={avatar}/>
                    </div>
                </Toolbar>
            </AppBar>
            <nav className={classes.drawer} aria-label="mailbox folders">
                {/* The implementation can be swapped with js to avoid SEO duplication of links. */}
                <Hidden smUp implementation="css">
                    <Drawer
                        container={container}
                        variant="temporary"
                        anchor={theme.direction === 'rtl' ? 'right' : 'left'}
                        open={mobileOpen}
                        onClose={handleDrawerToggle}
                        classes={{
                            paper: classes.drawerPaper,
                        }}
                        ModalProps={{
                            keepMounted: true, // Better open performance on mobile.
                        }}
                    >
                        {drawer}
                    </Drawer>
                </Hidden>
                <Hidden xsDown implementation="css">
                    <Drawer
                        classes={{
                            paper: classes.drawerPaper,
                        }}
                        variant="permanent"
                        open
                    >
                        {drawer}
                    </Drawer>
                </Hidden>
            </nav>
            <main className={classes.content}>
                <div className={classes.toolbar} />
                <div className={'skeleton-container'}>
                    {mainContentSelection}
                </div>
            </main>
        </div>
    )
}

export default Skeleton